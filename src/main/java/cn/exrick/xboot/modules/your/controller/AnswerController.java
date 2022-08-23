package cn.exrick.xboot.modules.your.controller;

import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.modules.your.entity.Answer;
import cn.exrick.xboot.modules.your.service.IAnswerService;
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
@Api(tags = "评论回复信息管理接口")
@RequestMapping("/xboot/answer")
@Transactional
public class AnswerController {

    @Autowired
    private IAnswerService iAnswerService;

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "通过id获取")
    public Result<List<Answer>> get(@PathVariable String id) {
        List<Answer> answers = iAnswerService.selectAllAnswerById(id);
        //Answer answer = iAnswerService.getById(id);
        return new ResultUtil<List<Answer>>().setData(answers);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "获取全部数据")
    public Result<List<Answer>> getAll() {

        List<Answer> list = iAnswerService.list();
        return new ResultUtil<List<Answer>>().setData(list);
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "分页获取")
    public Result<IPage<Answer>> getByPage(PageVo page) {

        IPage<Answer> data = iAnswerService.page(PageUtil.initMpPage(page));
        return new ResultUtil<IPage<Answer>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "编辑或更新数据")
    public Result<Answer> saveOrUpdate(Answer answer) {

        if (iAnswerService.saveOrUpdate(answer)) {
            return new ResultUtil<Answer>().setData(answer);
        }
        return new ResultUtil<Answer>().setErrorMsg("操作失败");
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "批量通过id删除")
    public Result delAllByIds(@RequestParam long[] ids) {

        for (long id : ids) {
            iAnswerService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }
}
