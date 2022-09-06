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
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xqp
 */
@Slf4j
@RestController
@Api(tags = "帖子评论信息管理接口")
@RequestMapping("/xboot/comment")
@CrossOrigin
@Transactional
public class CommentController {

    @Autowired
    private ICommentService iCommentService;

    //根据userid查询comment表
    public Result<List<Comment>> Post(@PathVariable long id) {
        List<Comment> info = iCommentService.getInfo(id);
        return new ResultUtil<List<Comment>>().setData(info);
    }

    //根据postId查询帖子信息
    public Result<List<Post>> Post(@PathVariable String postId) {
        List<Post> info = iCommentService.getPost(postId);
        return new ResultUtil<List<Post>>().setData(info);
    }

    //根据userid查询用户信息表
    public Result<List<UserInfo>> getUser(@PathVariable String userId) {
        List<UserInfo> info = iCommentService.getUserInfo(userId);
        return new ResultUtil<List<UserInfo>>().setData(info);
    }

    @ApiOperation(value = "评论帖子")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "int", name = "postId", value = "帖子Id", required = true),
            @ApiImplicitParam(dataType = "String", name = "commentContent", value = "评论内容", required = true),
            @ApiImplicitParam(dataType = "String", name = "nickName", value = "评论人昵称", required = true),
            @ApiImplicitParam(dataType = "int", name = "commentUserId", value = "评论人id", required = true),
            @ApiImplicitParam(dataType = "int", name = "postUserId", value = "发帖人id", required = true),
    })
    @RequestMapping(value = "/sendComment", method = RequestMethod.POST)
    public Result<String> Comment(@RequestParam("postId") int postId, @RequestParam("commentContent") String commentContent, @RequestParam("nickName") String nickName,
                                  @RequestParam("commentUserId") int commentUserId, @RequestParam("postUserId") int postUserId) {
        Comment comment = new Comment();
        comment.setCreateTime(new Date());
        comment.setCommentContent(commentContent);
        comment.setPostId(postId);
        comment.setNickName(nickName);
        comment.setPostUserId(postUserId);
        comment.setCommentUserId(commentUserId);
        iCommentService.newComment(comment.getPostId(), comment.getCommentContent(), comment.getNickName(), comment.getCommentUserId(), comment.getPostUserId(), comment.getCreateTime());
        String result = "评论回执";
        return new ResultUtil<String>().setData(result);
    }

    @ApiOperation(value = "帖子评论信息")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "long", name = "userid", value = "用户id", required = true),
    })
    @RequestMapping(value = "/getPostComment", method = RequestMethod.POST)
    public Result<List> getPostComment(@RequestParam("userid") long userid) {
        List<Comment> comment = iCommentService.getInfo(userid);
        List result = new ArrayList();
        for (int i = 0; i < comment.size(); i++) {
            String time = String.valueOf(comment.get(i).getCreateTime());
            result.add(0, time);
            result.add(comment.get(i));
            String postId = String.valueOf(comment.get(i).getPostId());
            result.add(iCommentService.getPost(postId).get(i));
            String targetUserid = String.valueOf(comment.get(i).getCommentUserId());
            result.add(iCommentService.getUserInfo(targetUserid).get(i));
        }
        return new ResultUtil<List>().setData(result);
    }

    /**
     * 根据帖子id来获取帖子所有评论信息
     *
     * @param postId
     * @return
     */
    @ApiOperation(value = "获取帖子所有评论")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "long", name = "postId", value = "帖子id", required = true),
    })
    @RequestMapping(value = "/getComment", method = RequestMethod.POST)
    public Result<List> getComment(@RequestParam("postId") long postId) {
        List<Comment> comment = iCommentService.getCommentByPostId(postId);
        List result = new ArrayList();
        for (int i = 0; i < comment.size(); i++) {
            result.add(comment.get(i));
            String userid = String.valueOf(comment.get(i).getCommentUserId());
            result.add(iCommentService.getUserInfo(userid).get(0));
        }
        return new ResultUtil<List>().setData(result);
    }

    @ApiOperation(value = "根据评论id获取对应回复")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "long", name = "commentId", value = "评论id", required = true),
    })
    @RequestMapping(value = "/getAnswer", method = RequestMethod.GET)
    public Result<List> getAnswer(@RequestParam("commentId") long commentId) {
        List<Answer> answer = iCommentService.getAnswerByCommentId(String.valueOf(commentId));
        List res = new ArrayList();
        for (int i = 0; i < answer.size(); i++) {
            res.add(answer.get(i));
        }
        return new ResultUtil<List>().setData(res);
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
