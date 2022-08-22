package cn.exrick.xboot.modules.file.service;

import cn.exrick.xboot.base.XbootBaseService;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.SearchVo;
import cn.exrick.xboot.modules.file.entity.File;
import org.springframework.data.domain.Page;

/**
 * 文件管理接口
 * @author Exrick
 */
public interface FileService extends XbootBaseService<File, String> {

    /**
     * 多条件获取列表
     * @param file
     * @param searchVo
     * @param pageVo
     * @param getCurrUser
     * @return
     */
    Page<File> getFileList(File file, SearchVo searchVo, PageVo pageVo, Boolean getCurrUser);

    /**
     * 回收站操作
     * @param id
     * @param getCurrUser
     */
    void trash(String id, Boolean getCurrUser);

    /**
     * 收藏
     * @param id
     * @param getCurrUser
     */
    void collect(String id, Boolean getCurrUser);

    /**
     * 重命名
     * @param id
     * @param newKey
     * @param newTitle
     * @param getCurrUser
     */
    void rename(String id, String newKey, String newTitle, Boolean getCurrUser);

    /**
     * 创建副本
     * @param id
     * @param getCurrUser
     */
    void copy(String id, Boolean getCurrUser);

    /**
     * 删除
     * @param ids
     * @param getCurrUser
     */
    void delete(String[] ids, Boolean getCurrUser);

    /**
     * 清空回收站
     */
    void clearTrash();

    /**
     * 通过categoryId删除
     * @param categoryId
     */
    void deleteByCategoryId(String categoryId);
}