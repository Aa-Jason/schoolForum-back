package cn.exrick.xboot.modules.your.controller;

import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.modules.your.entity.*;
import cn.exrick.xboot.modules.your.service.ICommentService;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    //根据userid查询comment表
    public Result<List<Comment>> Post(@PathVariable long id){
        List<Comment> info = iCommentService.getInfo(id);
        return new ResultUtil<List<Comment>>().setData(info);
    }

    //根据postId查询帖子信息
    public Result<List<Post>> Post(@PathVariable String postId){
        List<Post> info = iCommentService.getPost(postId);
        return new ResultUtil<List<Post>>().setData(info);
    }

    //根据userid查询用户信息表
    public Result<List<UserInfo>> getUser(@PathVariable String userId){
        List<UserInfo> info = iCommentService.getUserInfo(userId);
        return new ResultUtil<List<UserInfo>>().setData(info);
    }

    @ApiOperation(value="帖子评论信息")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "long",name="userid",value="用户id",required = true),
    })
    @RequestMapping(value = "/getPostComment", method = RequestMethod.GET)
    public Result<List> getPostComment(@RequestParam("userid") long userid) {
        List<Comment> comment = iCommentService.getInfo(userid);
        int a = comment.size();
        List result = new ArrayList();

        for(int i = 0;i<comment.size();i++){
            result.add(comment.get(i));
            String postId = comment.get(i).getPostId();
            result.add(iCommentService.getPost(postId).get(i));
            String targetUserid=comment.get(i).getPostUserId();
            result.add(iCommentService.getUserInfo(targetUserid).get(i));
        }
        return new ResultUtil<List>().setData(result);
    }

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
