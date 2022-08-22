package cn.exrick.xboot.modules.file.service;

import cn.exrick.xboot.base.XbootBaseService;
import cn.exrick.xboot.modules.file.entity.FileCategory;

import java.util.List;

/**
 * 文件分类接口
 * @author Exrick
 */
public interface FileCategoryService extends XbootBaseService<FileCategory, String> {

    /**
     * 多条件获取
     * @param fileCategory
     * @param sort
     * @param order
     * @return
     */
    List<FileCategory> findByCondition(FileCategory fileCategory, String sort, String order);

    /**
     * 通过父id和创建人获取
     * @param parentId
     * @param delFlag
     * @param createBy
     * @return
     */
    List<FileCategory> findByParentId(String parentId, Integer delFlag, String createBy);

    /**
     * 通过名称和创建人模糊搜索
     * @param title
     * @param createBy
     * @return
     */
    List<FileCategory> findByTitleLikeAndCreateBy(String title, String createBy);
}