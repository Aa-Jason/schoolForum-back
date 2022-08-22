package cn.exrick.xboot.modules.file.controller;

import cn.exrick.xboot.common.constant.CommonConstant;
import cn.exrick.xboot.common.exception.XbootException;
import cn.exrick.xboot.common.redis.RedisTemplateHelper;
import cn.exrick.xboot.common.utils.CommonUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.utils.SecurityUtil;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.modules.base.entity.User;
import cn.exrick.xboot.modules.file.entity.File;
import cn.exrick.xboot.modules.file.entity.FileCategory;
import cn.exrick.xboot.modules.file.service.FileCategoryService;
import cn.exrick.xboot.modules.file.service.FileService;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Exrick
 */
@Slf4j
@RestController
@Api(tags = "文件分类管理接口")
@RequestMapping("/xboot/fileCategory")
@Transactional
public class FileCategoryController {

    @Autowired
    private FileCategoryService fileCategoryService;

    @Autowired
    private FileService fileService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private RedisTemplateHelper redisTemplate;

    @RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "多条件获取")
    public Result<List<FileCategory>> getByCondition(@RequestParam(required = false) String parentId,
                                                     @RequestParam(required = false) String title,
                                                     @RequestParam(required = false, defaultValue = "0") Integer delFlag,
                                                     @RequestParam(required = false) Boolean isCollect,
                                                     @RequestParam(required = false, defaultValue = "sortOrder") String sort,
                                                     @RequestParam(required = false, defaultValue = "asc") String order) {

        User user = securityUtil.getCurrUserSimple();
        FileCategory fileCategory = new FileCategory().setParentId(parentId).setIsCollect(isCollect).setTitle(title);
        fileCategory.setCreateBy(user.getUsername());
        fileCategory.setDelFlag(delFlag);
        List<FileCategory> list = fileCategoryService.findByCondition(fileCategory, sort, order);
        setInfo(list);
        return new ResultUtil<List<FileCategory>>().setData(list);
    }

    @RequestMapping(value = "/trash", method = RequestMethod.POST)
    @ApiOperation(value = "用户文件夹回收站操作")
    public Result trash(@RequestParam String id) {

        FileCategory fileCategory = fileCategoryService.get(id);
        User user = securityUtil.getCurrUserSimple();
        if (!user.getUsername().equals(fileCategory.getCreateBy())) {
            throw new XbootException("你无权操作此文件夹");
        }
        fileCategory.setDelFlag(CommonConstant.DEL_FLAG_TRASH.equals(fileCategory.getDelFlag())
                ? CommonConstant.DEL_FLAG_FALSE : CommonConstant.DEL_FLAG_TRASH);
        fileCategoryService.update(fileCategory);
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/collect", method = RequestMethod.POST)
    @ApiOperation(value = "用户文件夹收藏")
    public Result collect(@RequestParam String id) {

        FileCategory fileCategory = fileCategoryService.get(id);

        User user = securityUtil.getCurrUserSimple();
        if (!user.getUsername().equals(fileCategory.getCreateBy())) {
            throw new XbootException("你无权操作此文件夹");
        }
        fileCategory.setIsCollect(fileCategory.getIsCollect() == null ? true : !fileCategory.getIsCollect());
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/rename", method = RequestMethod.POST)
    @ApiOperation(value = "用户文件夹重命名")
    public Result rename(@RequestParam String id,
                                 @RequestParam String newTitle) {

        FileCategory fileCategory = fileCategoryService.get(id);

        User user = securityUtil.getCurrUserSimple();
        if (!user.getUsername().equals(fileCategory.getCreateBy())) {
            throw new XbootException("你无权操作此文件夹");
        }
        fileCategory.setTitle(newTitle);
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation(value = "添加")
    public Result add(FileCategory fileCategory) {

        fileCategoryService.save(fileCategory);
        // 如果不是添加的一级 判断设置上级为父节点标识
        if (!CommonConstant.PARENT_ID.equals(fileCategory.getParentId())) {
            FileCategory parent = fileCategoryService.get(fileCategory.getParentId());
            if (parent.getIsParent() == null || !parent.getIsParent()) {
                parent.setIsParent(true);
                fileCategoryService.update(parent);
            }
        }
        return ResultUtil.success("添加成功");
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ApiOperation(value = "编辑")
    public Result edit(FileCategory fileCategory) {

        if (fileCategory.getId().equals(fileCategory.getParentId())) {
            return ResultUtil.error("上级节点不能为自己");
        }
        User user = securityUtil.getCurrUserSimple();
        FileCategory old = fileCategoryService.get(fileCategory.getId());
        if (!user.getUsername().equals(old.getCreateBy())) {
            return ResultUtil.error("你无权操作此文件夹");
        }
        String oldParentId = old.getParentId();
        fileCategoryService.update(fileCategory);
        // 如果该节点不是一级节点 且修改了级别 判断上级还有无子节点
        if (!CommonConstant.PARENT_ID.equals(oldParentId) && !oldParentId.equals(fileCategory.getParentId())) {
            FileCategory parent = fileCategoryService.get(oldParentId);
            List<FileCategory> children = fileCategoryService
                    .findByParentId(parent.getId(), CommonConstant.DEL_FLAG_FALSE, user.getUsername());
            if (parent != null && (children == null || children.isEmpty())) {
                parent.setIsParent(false);
                fileCategoryService.update(parent);
            }
        }
        return ResultUtil.success("编辑成功");
    }

    @RequestMapping(value = "/moveByIds", method = RequestMethod.POST)
    @ApiOperation(value = "批量修改分类（移动）")
    public Result moveByIds(@RequestParam String[] ids,
                                    @RequestParam String categoryId) {

        User user = securityUtil.getCurrUserSimple();
        if (!CommonConstant.PARENT_ID.equals(categoryId)) {
            FileCategory fileCategory = fileCategoryService.get(categoryId);
            if (!user.getUsername().equals(fileCategory.getCreateBy())) {
                return ResultUtil.error("你无权移动至该文件夹");
            }
        }
        for (String id : ids) {
            File file = fileService.get(id);
            if (categoryId.equals(file.getCategoryId())) {
                // 分类没变化 无需移动
                continue;
            }
            if (!user.getUsername().equals(file.getCreateBy())) {
                return ResultUtil.error("你无权操作此文件");
            }
            file.setCategoryId(categoryId);
            fileService.save(file);
            redisTemplate.delete("file::" + id);
        }
        return ResultUtil.success("批量移动文件成功");
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "批量通过id删除")
    public Result delByIds(@RequestParam String[] ids) {

        User user = securityUtil.getCurrUserSimple();
        for (String id : ids) {
            deleteRecursion(id, ids, user.getUsername());
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }

    @RequestMapping(value = "/clearTrash", method = RequestMethod.GET)
    @ApiOperation(value = "清空回收站")
    public Result clearTrash() {

        User user = securityUtil.getCurrUserSimple();
        // 获取其回收站
        List<FileCategory> list = fileCategoryService
                .findByParentId(null, CommonConstant.DEL_FLAG_TRASH, user.getUsername());
        String[] ids = list.stream().map(FileCategory::getId).toArray(String[]::new);
        for (String id : ids) {
            deleteRecursion(id, ids, user.getUsername());
        }
        return ResultUtil.success("清空文件夹回收站成功");
    }

    public void deleteRecursion(String id, String[] ids, String username) {

        // 获得其父节点
        FileCategory o = fileCategoryService.get(id);
        if (!username.equals(o.getCreateBy())) {
            throw new XbootException("你无权操作此文件夹");
        }
        FileCategory parent = null;
        if (StrUtil.isNotBlank(o.getParentId())) {
            parent = fileCategoryService.findById(o.getParentId());
        }
        fileCategoryService.delete(id);
        fileService.deleteByCategoryId(id);
        // 判断父节点是否还有子节点
        if (parent != null) {
            List<FileCategory> children = fileCategoryService.findByParentId(parent.getId(), null, username);
            if (children == null || children.isEmpty()) {
                parent.setIsParent(false);
                fileCategoryService.update(parent);
            }
        }
        // 递归删除
        List<FileCategory> objs = fileCategoryService.findByParentId(id, null, username);
        for (FileCategory obj : objs) {
            if (!CommonUtil.judgeIds(obj.getId(), ids)) {
                deleteRecursion(obj.getId(), ids, username);
            }
        }
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ApiOperation(value = "名称模糊搜索")
    public Result<List<FileCategory>> searchByTitle(@RequestParam String title) {

        User user = securityUtil.getCurrUserSimple();
        List<FileCategory> list = fileCategoryService.findByTitleLikeAndCreateBy("%" + title + "%", user.getUsername());
        setInfo(list);
        return new ResultUtil<List<FileCategory>>().setData(list);
    }

    public void setInfo(List<FileCategory> list) {

        // lambda表达式
        list.forEach(item -> {
            if (!CommonConstant.PARENT_ID.equals(item.getParentId())) {
                FileCategory parent = fileCategoryService.get(item.getParentId());
                item.setParentTitle(parent.getTitle());
            } else {
                item.setParentTitle("一级目录");
            }
        });
    }
}
