package cn.exrick.xboot.modules.file.dao;

import cn.exrick.xboot.base.XbootBaseDao;
import cn.exrick.xboot.modules.file.entity.File;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 文件管理数据处理层
 * @author Exrick
 */
public interface FileDao extends XbootBaseDao<File, String> {

    /**
     * 清空回收站
     * @param username
     * @param delFlag
     */
    @Modifying
    @Query("delete from File f where f.createBy = ?1 and f.delFlag = ?2")
    void deleteByCreateByAndDelFlag(String username, Integer delFlag);

    /**
     * 通过categoryId删除
     * @param categoryId
     */
    @Modifying
    @Query("delete from File f where f.categoryId = ?1")
    void deleteByCategoryId(String categoryId);
}