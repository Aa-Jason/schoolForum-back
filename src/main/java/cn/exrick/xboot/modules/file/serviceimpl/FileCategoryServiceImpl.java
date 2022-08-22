package cn.exrick.xboot.modules.file.serviceimpl;

import cn.exrick.xboot.common.constant.CommonConstant;
import cn.exrick.xboot.modules.file.dao.FileCategoryDao;
import cn.exrick.xboot.modules.file.entity.FileCategory;
import cn.exrick.xboot.modules.file.service.FileCategoryService;
import cn.exrick.xboot.modules.file.utils.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件分类接口实现
 * @author Exrick
 */
@Slf4j
@Service
@Transactional
public class FileCategoryServiceImpl implements FileCategoryService {

    @Autowired
    private FileCategoryDao fileCategoryDao;

    @Override
    public FileCategoryDao getRepository() {
        return fileCategoryDao;
    }

    @Override
    public List<FileCategory> findByCondition(FileCategory fileCategory, String sort, String order) {

        Sort.Direction d;
        if (StrUtil.isBlank(order)) {
            d = Sort.Direction.DESC;
        } else {
            d = Sort.Direction.valueOf(order.toUpperCase());
        }
        if (StrUtil.isBlank(sort)) {
            sort = "sortOrder";
        }
        List<FileCategory> list = fileCategoryDao.findAll(new Specification<FileCategory>() {
            @Override
            public Predicate toPredicate(Root<FileCategory> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

                Path<String> parentIdField = root.get("parentId");
                Path<String> titleField = root.get("title");
                Path<Integer> delFlagField = root.get("delFlag");
                Path<Boolean> isCollectField = root.get("isCollect");
                Path<String> createByField = root.get("createBy");

                List<Predicate> list = new ArrayList<>();

                if (StrUtil.isNotBlank(fileCategory.getParentId())) {
                    list.add(cb.equal(parentIdField, fileCategory.getParentId()));
                }

                if (StrUtil.isNotBlank(fileCategory.getTitle())) {
                    list.add(cb.like(titleField, "%" + fileCategory.getTitle() + "%"));
                }

                if (fileCategory.getDelFlag() != null) {
                    list.add(cb.equal(delFlagField, fileCategory.getDelFlag()));
                }
                if (fileCategory.getIsCollect() != null) {
                    list.add(cb.equal(isCollectField, fileCategory.getIsCollect()));
                }

                if (StrUtil.isNotBlank(fileCategory.getCreateBy())) {
                    list.add(cb.equal(createByField, fileCategory.getCreateBy()));
                }

                Predicate[] arr = new Predicate[list.size()];
                cq.where(list.toArray(arr));
                return null;
            }
        }, Sort.by(d, sort));

        if (CommonConstant.DEL_FLAG_TRASH.equals(fileCategory.getDelFlag())) {
            list.forEach(e -> {
                if (FileUtil.isOvertime(e.getUpdateTime())) {
                    delete(e.getId());
                } else {
                    e.setRestDelTime(FileUtil.getRestTime(e.getUpdateTime()));
                }
            });
            list = list.stream().filter(fc -> !FileUtil.isOvertime(fc.getUpdateTime())).collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public List<FileCategory> findByParentId(String parentId, Integer delFlag, String createBy) {

        FileCategory fileCategory = new FileCategory().setParentId(parentId);
        fileCategory.setDelFlag(delFlag);
        fileCategory.setCreateBy(createBy);
        return findByCondition(fileCategory, null, null);
    }

    @Override
    public List<FileCategory> findByTitleLikeAndCreateBy(String title, String createBy) {

        return fileCategoryDao.findByTitleLikeAndCreateByOrderBySortOrder(title, createBy);
    }
}