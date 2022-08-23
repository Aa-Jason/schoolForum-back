package cn.exrick.xboot.modules.your.controller;

import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.modules.your.entity.Support;
import cn.exrick.xboot.modules.your.service.ISupportService;
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
 * @author xqp
 */
@Slf4j
@RestController
@Api(tags = "点赞信息管理接口")
@RequestMapping("/xboot/support")
@Transactional
public class SupportController {

    @Autowired
    private ISupportService iSupportService;

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "通过id获取")
    public Result<List<Support>> get(@PathVariable String id) {
        List<Support> supports = iSupportService.selectAllSupportById(id);
        //Support support = iSupportService.getById(id);
        return new ResultUtil<List<Support>>().setData(supports);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "获取全部数据")
    public Result<List<Support>> getAll() {

        List<Support> list = iSupportService.list();
        return new ResultUtil<List<Support>>().setData(list);
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "分页获取")
    public Result<IPage<Support>> getByPage(PageVo page) {

        IPage<Support> data = iSupportService.page(PageUtil.initMpPage(page));
        return new ResultUtil<IPage<Support>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "编辑或更新数据")
    public Result<Support> saveOrUpdate(Support support) {

        if (iSupportService.saveOrUpdate(support)) {
            return new ResultUtil<Support>().setData(support);
        }
        return new ResultUtil<Support>().setErrorMsg("操作失败");
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "批量通过id删除")
    public Result delAllByIds(@RequestParam long[] ids) {

        for (long id : ids) {
            iSupportService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }
}
