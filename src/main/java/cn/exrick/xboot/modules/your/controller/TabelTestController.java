package cn.exrick.xboot.modules.your.controller;

import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.modules.your.entity.TabelTest;
import cn.exrick.xboot.modules.your.service.ITabelTestService;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Exrick
 */
@Slf4j
@RestController
@Api(tags = "建表测试管理接口")
@RequestMapping("/xboot/tabelTest")
@Transactional
public class TabelTestController {

    @Autowired
    private ITabelTestService iTabelTestService;

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "通过id获取")
    public Result<TabelTest> get(@PathVariable String id) {
        TabelTest tabelTest = iTabelTestService.getById(id);
        return new ResultUtil<TabelTest>().setData(tabelTest);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "获取全部数据")
    public Result<List<TabelTest>> getAll() {

        List<TabelTest> list = iTabelTestService.list();
        return new ResultUtil<List<TabelTest>>().setData(list);
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "分页获取")
    public Result<IPage<TabelTest>> getByPage(PageVo page) {

        IPage<TabelTest> data = iTabelTestService.page(PageUtil.initMpPage(page));
        return new ResultUtil<IPage<TabelTest>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "编辑或更新数据")
    public Result<TabelTest> saveOrUpdate(TabelTest tabelTest) {

        if (iTabelTestService.saveOrUpdate(tabelTest)) {
            return new ResultUtil<TabelTest>().setData(tabelTest);
        }
        return new ResultUtil<TabelTest>().setErrorMsg("操作失败");
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "批量通过id删除")
    public Result delAllByIds(@RequestParam String[] ids) {

        for (String id : ids) {
            iTabelTestService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }
}
