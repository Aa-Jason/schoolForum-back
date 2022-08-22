package cn.exrick.xboot.modules.file.serviceimpl;

import cn.exrick.xboot.common.constant.CommonConstant;
import cn.exrick.xboot.common.constant.OssConstant;
import cn.exrick.xboot.common.constant.SettingConstant;
import cn.exrick.xboot.common.exception.XbootException;
import cn.exrick.xboot.common.redis.RedisTemplateHelper;
import cn.exrick.xboot.common.utils.CommonUtil;
import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.SecurityUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.SearchVo;
import cn.exrick.xboot.modules.base.entity.User;
import cn.exrick.xboot.modules.base.service.SettingService;
import cn.exrick.xboot.modules.base.vo.OssSetting;
import cn.exrick.xboot.modules.file.dao.FileDao;
import cn.exrick.xboot.modules.file.entity.File;
import cn.exrick.xboot.modules.file.manage.FileManageFactory;
import cn.exrick.xboot.modules.file.service.FileService;
import cn.exrick.xboot.modules.file.utils.FileUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 文件管理接口实现
 * @author Exrick
 */
@Slf4j
@Service
@Transactional
@CacheConfig(cacheNames = "file")
public class FileServiceImpl implements FileService {

    @Autowired
    private FileDao fileDao;

    @Autowired
    private RedisTemplateHelper redisTemplate;

    @Autowired
    private SecurityUtil securityUtil;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SettingService settingService;

    @Autowired
    private FileManageFactory fileManageFactory;

    @Override
    public FileDao getRepository() {
        return fileDao;
    }

    @Override
    @Cacheable(key = "#id")
    public File get(String id) {

        // 避免缓存穿透
        String result = redisTemplate.get("file::" + id);
        if ("null".equals(result)) {
            return null;
        }
        File file = fileDao.findById(id).orElse(null);
        if (file == null) {
            redisTemplate.set("file::" + id, "null", 5L, TimeUnit.MINUTES);
        }
        return file;
    }

    @Override
    public Page<File> getFileList(File file, SearchVo searchVo, PageVo pageVo, Boolean getCurrUser) {

        if (getCurrUser) {
            file.setCreateBy(securityUtil.getCurrUserSimple().getUsername());
        }

        Page<File> page = fileDao.findAll(new Specification<File>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<File> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

                Path<String> titleField = root.get("title");
                Path<String> fKeyField = root.get("fKey");
                Path<String> typeField = root.get("type");
                Path<String> categoryIdField = root.get("categoryId");
                Path<String> createByField = root.get("createBy");
                Path<Boolean> isCollectField = root.get("isCollect");
                Path<Integer> locationField = root.get("location");
                Path<Integer> delFlagField = root.get("delFlag");
                Path<Date> createTimeField = root.get("createTime");

                List<Predicate> list = new ArrayList<>();

                // 模糊搜素
                if (StrUtil.isNotBlank(file.getTitle())) {
                    list.add(cb.like(titleField, '%' + file.getTitle() + '%'));
                }
                if (StrUtil.isNotBlank(file.getFKey())) {
                    list.add(cb.like(fKeyField, '%' + file.getFKey() + '%'));
                }
                if (StrUtil.isNotBlank(file.getType())) {
                    list.add(cb.like(typeField, '%' + file.getType() + '%'));
                }
                if (StrUtil.isNotBlank(file.getCreateBy())) {
                    list.add(cb.like(createByField, '%' + file.getCreateBy() + '%'));
                }

                if (file.getDelFlag() != null) {
                    list.add(cb.equal(delFlagField, file.getDelFlag()));
                }
                if (file.getLocation() != null) {
                    list.add(cb.equal(locationField, file.getLocation()));
                }
                if (file.getIsCollect() != null) {
                    list.add(cb.equal(isCollectField, file.getIsCollect()));
                }

                if (StrUtil.isNotBlank(file.getCategoryId())) {
                    list.add(cb.equal(categoryIdField, file.getCategoryId()));
                }

                // 创建时间
                if (StrUtil.isNotBlank(searchVo.getStartDate()) && StrUtil.isNotBlank(searchVo.getEndDate())) {
                    Date start = DateUtil.parse(searchVo.getStartDate());
                    Date end = DateUtil.parse(searchVo.getEndDate());
                    list.add(cb.between(createTimeField, start, DateUtil.endOfDay(end)));
                }

                Predicate[] arr = new Predicate[list.size()];
                cq.where(list.toArray(arr));
                return null;
            }
        }, PageUtil.initPage(pageVo));

        OssSetting os = new Gson().fromJson(settingService.get(SettingConstant.LOCAL_OSS).getValue(), OssSetting.class);
        for (File e : page.getContent()) {
            if (e.getLocation() != null && OssConstant.OSS_LOCAL.equals(e.getLocation())) {
                String url = os.getHttp() + os.getEndpoint() + "/";
                entityManager.detach(e);
                e.setUrl(url + e.getId());
            }
            if (CommonConstant.DEL_FLAG_TRASH.equals(file.getDelFlag())) {
                if (FileUtil.isOvertime(e.getUpdateTime())) {
                    delete(new String[]{e.getId()}, true);
                } else {
                    e.setRestDelTime(FileUtil.getRestTime(e.getUpdateTime()));
                }
            }
        }

        if (CommonConstant.DEL_FLAG_TRASH.equals(file.getDelFlag())) {
            page = new PageImpl<>(page.getContent().stream().filter(e -> !FileUtil.isOvertime(e.getUpdateTime())).collect(Collectors.toList()),
                    page.getPageable(), page.getTotalPages());
        }
        return page;
    }

    @Override
    public void trash(String id, Boolean getCurrUser) {

        File file = get(id);
        if (file.getLocation() == null) {
            throw new XbootException("存储位置未知");
        }

        if (getCurrUser) {
            User user = securityUtil.getCurrUserSimple();
            if (!user.getUsername().equals(file.getCreateBy())) {
                throw new XbootException("你无权操作此文件");
            }
        }
        file.setDelFlag(CommonConstant.DEL_FLAG_TRASH.equals(file.getDelFlag())
                ? CommonConstant.DEL_FLAG_FALSE : CommonConstant.DEL_FLAG_TRASH);
        fileDao.saveAndFlush(file);
    }

    @Override
    public void collect(String id, Boolean getCurrUser) {

        File file = get(id);
        if (file.getLocation() == null) {
            throw new XbootException("存储位置未知");
        }

        if (getCurrUser) {
            User user = securityUtil.getCurrUserSimple();
            if (!user.getUsername().equals(file.getCreateBy())) {
                throw new XbootException("你无权操作此文件");
            }
        }
        file.setIsCollect(file.getIsCollect() == null ? true : !file.getIsCollect());
        fileDao.saveAndFlush(file);
    }

    @Override
    @CacheEvict(key = "#id")
    public void rename(String id, String newKey, String newTitle, Boolean getCurrUser) {

        File file = get(id);
        if (file.getLocation() == null) {
            throw new XbootException("存储位置未知");
        }

        if (getCurrUser) {
            User user = securityUtil.getCurrUserSimple();
            if (!user.getUsername().equals(file.getCreateBy())) {
                throw new XbootException("你无权操作此文件");
            }
        }

        String newUrl = "", oldKey = file.getFKey();
        if (StrUtil.isBlank(newKey)) {
            newKey = file.getFKey();
        }
        if (StrUtil.isBlank(newTitle)) {
            newTitle = file.getTitle();
        }

        if (!oldKey.equals(newKey)) {
            // 特殊处理本地服务器
            if (OssConstant.OSS_LOCAL.equals(file.getLocation())) {
                oldKey = file.getUrl();
            }
            newUrl = fileManageFactory.getFileManage(file.getLocation()).renameFile(oldKey, newKey);
            file.setUrl(newUrl);
        }

        file.setTitle(newTitle).setFKey(newKey);
        update(file);
    }

    @Override
    public void copy(String id, Boolean getCurrUser) {

        File file = get(id);
        if (file.getLocation() == null) {
            throw new XbootException("存储位置未知");
        }

        if (getCurrUser) {
            User user = securityUtil.getCurrUserSimple();
            if (!user.getUsername().equals(file.getCreateBy())) {
                throw new XbootException("你无权操作此文件");
            }
        }

        String key = file.getFKey();
        String toKey = CommonUtil.renamePic(key);
        // 特殊处理本地服务器
        if (OssConstant.OSS_LOCAL.equals(file.getLocation())) {
            key = file.getUrl();
        }
        String newUrl = fileManageFactory.getFileManage(file.getLocation()).copyFile(key, toKey);
        File newFile = new File().setTitle("副本_" + file.getTitle()).setFKey(toKey).setSize(file.getSize()).setType(file.getType())
                .setLocation(file.getLocation()).setUrl(newUrl);
        if (getCurrUser) {
            newFile.setCategoryId(file.getCategoryId());
        }
        save(newFile);
    }

    @Override
    public void delete(String[] ids, Boolean getCurrUser) {

        for (String id : ids) {
            File file = get(id);
            if (file.getLocation() == null) {
                throw new XbootException("存储位置未知");
            }

            if (getCurrUser) {
                User user = securityUtil.getCurrUserSimple();
                if (!user.getUsername().equals(file.getCreateBy())) {
                    throw new XbootException("你无权操作此文件");
                }
            }

            // 特殊处理本地服务器
            String key = file.getFKey();
            if (OssConstant.OSS_LOCAL.equals(file.getLocation())) {
                key = file.getUrl();
            }
            try {
                fileManageFactory.getFileManage(file.getLocation()).deleteFile(key);
            } catch (Exception e) {
                log.error("服务器删除文件失败，ID：" + file.getId() + " 存储位置：" + file.getLocation());
            }
            delete(id);
            redisTemplate.delete("file::" + id);
        }
    }

    @Override
    public void clearTrash() {

        User user = securityUtil.getCurrUserSimple();
        fileDao.deleteByCreateByAndDelFlag(user.getUsername(), CommonConstant.DEL_FLAG_TRASH);
    }

    @Override
    public void deleteByCategoryId(String categoryId) {

        fileDao.deleteByCategoryId(categoryId);
    }
}