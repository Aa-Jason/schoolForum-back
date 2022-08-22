package cn.exrick.xboot.modules.file.controller;

import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.common.vo.SearchVo;
import cn.exrick.xboot.modules.file.entity.File;
import cn.exrick.xboot.modules.file.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


/**
 * @author Exrick
 */
@Slf4j
@RestController
@Api(tags = "文件管理管理接口")
@RequestMapping("/xboot/file")
@Transactional
public class FileController {

    @Autowired
    private FileService fileService;

    @RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取")
    public Result<Page<File>> getFileList(File file, SearchVo searchVo, PageVo pageVo) {

        Page<File> page = fileService.getFileList(file, searchVo, pageVo, false);
        return new ResultUtil<Page<File>>().setData(page);
    }

    @RequestMapping(value = "/rename", method = RequestMethod.POST)
    @ApiOperation(value = "文件重命名")
    public Result renameUserFile(@RequestParam String id,
                                         @RequestParam String newKey,
                                         @RequestParam String newTitle) {

        fileService.rename(id, newKey, newTitle, false);
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    @ApiOperation(value = "文件复制")
    public Result copy(@RequestParam String id) {

        fileService.copy(id, false);
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ApiOperation(value = "文件删除")
    public Result delete(@RequestParam String[] ids) {

        fileService.delete(ids, false);
        return ResultUtil.data(null);
    }
}
