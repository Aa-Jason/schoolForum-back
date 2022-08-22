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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Exrick
 */
@Slf4j
@RestController
@Api(tags = "文件管理管理接口")
@RequestMapping("/xboot/file/user")
@Transactional
public class FileUserController {

    @Autowired
    private FileService fileService;

    @RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "用户多条件分页获取")
    public Result<Page<File>> getFileList(File file, SearchVo searchVo, PageVo pageVo) {

        Page<File> page = fileService.getFileList(file, searchVo, pageVo, true);
        return new ResultUtil<Page<File>>().setData(page);
    }

    @RequestMapping(value = "/trash", method = RequestMethod.POST)
    @ApiOperation(value = "用户文件回收站操作")
    public Result trash(@RequestParam String[] ids) {

        for (String id : ids) {
            fileService.trash(id, true);
        }
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/collect", method = RequestMethod.POST)
    @ApiOperation(value = "用户文件收藏")
    public Result collect(@RequestParam String id) {

        fileService.collect(id, true);
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/rename", method = RequestMethod.POST)
    @ApiOperation(value = "用户文件重命名")
    public Result renameUserFile(@RequestParam String id,
                                         @RequestParam String newTitle) {

        fileService.rename(id, null, newTitle, true);
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    @ApiOperation(value = "用户文件复制")
    public Result copy(@RequestParam String id) {

        fileService.copy(id, true);
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ApiOperation(value = "用户文件删除")
    public Result delete(@RequestParam String[] ids) {

        fileService.delete(ids, true);
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/clearTrash", method = RequestMethod.GET)
    @ApiOperation(value = "清空回收站")
    public Result clearTrash() {

        fileService.clearTrash();
        return ResultUtil.success("清空文件回收站成功");
    }
}
