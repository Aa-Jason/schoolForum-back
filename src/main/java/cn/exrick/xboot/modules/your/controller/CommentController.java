package cn.exrick.xboot.modules.your.controller;

import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.modules.your.entity.Comment;
import cn.exrick.xboot.modules.your.service.ICommentService;
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
@Api(tags = "帖子评论信息管理接口")
@RequestMapping("/xboot/comment")
@Transactional
public class CommentController {

    @Autowired
    private ICommentService iCommentService;

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "通过id获取")
    public Result<Comment> get(@PathVariable long id) {

        Comment comment = iCommentService.getById(id);
        return new ResultUtil<Comment>().setData(comment);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "获取全部数据")
    public Result<List<Comment>> getAll() {

        List<Comment> list = iCommentService.list();
        return new ResultUtil<List<Comment>>().setData(list);
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "分页获取")
    public Result<IPage<Comment>> getByPage(PageVo page) {

        IPage<Comment> data = iCommentService.page(PageUtil.initMpPage(page));
        return new ResultUtil<IPage<Comment>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "编辑或更新数据")
    public Result<Comment> saveOrUpdate(Comment comment) {

        if (iCommentService.saveOrUpdate(comment)) {
            return new ResultUtil<Comment>().setData(comment);
        }
        return new ResultUtil<Comment>().setErrorMsg("操作失败");
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "批量通过id删除")
    public Result delAllByIds(@RequestParam long[] ids) {

        for (long id : ids) {
            iCommentService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }
}
