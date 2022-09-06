package cn.exrick.xboot.modules.your.controller;

import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.modules.your.entity.*;
import cn.exrick.xboot.modules.your.service.IAnswerService;
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
import java.util.Date;
import java.util.List;

/**
 * @author xqp
 */
@Slf4j
@RestController
@Api(tags = "评论回复信息管理接口")
@RequestMapping("/xboot/answer")
@CrossOrigin
@Transactional
public class AnswerController {

    @Autowired
    private IAnswerService iAnswerService;


    //根据userid查询answer表,a用于区分回复回复和回复评论,1为回复，2为评论
    public Result<List<Answer>> Post(@PathVariable long id,@PathVariable int a){
        List<Answer> info = iAnswerService.getInfo(id,a);
        return new ResultUtil<List<Answer>>().setData(info);
    }

    //根据postId查询帖子信息
    public Result<List<Post>> Post(@PathVariable String postId){
        List<Post> info = iAnswerService.getPost(postId);
        return new ResultUtil<List<Post>>().setData(info);
    }

    //根据userid查询用户信息表
    public Result<List<UserInfo>> getUser(@PathVariable String userId){
        List<UserInfo> info = iAnswerService.getUserInfo(userId);
        return new ResultUtil<List<UserInfo>>().setData(info);
    }

    //根据commentId查询评论信息表
    public Result<List<Comment>> getComment(@PathVariable String commentId){
        List<Comment> info = iAnswerService.getCommentInfo(commentId);
        return new ResultUtil<List<Comment>>().setData(info);
    }

    //根据answerId查询回复表
    public Result<List<Answer>> getTargetAnswer(@PathVariable String answerId){
        List<Answer> info = iAnswerService.getAnswerInfo(answerId);
        return new ResultUtil<List<Answer>>().setData(info);
    }

    @ApiOperation(value="回复评论")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "int",name="postId",value="帖子Id",required = true),
            @ApiImplicitParam(dataType = "String",name="answerContent",value="回复内容",required = true),
            @ApiImplicitParam(dataType = "String",name="nickName",value="回复人昵称",required = true),
            @ApiImplicitParam(dataType = "int",name="answerUserId",value="回复人id",required = true),
            @ApiImplicitParam(dataType = "int",name="targetAnswerId",value="目标回复id",required = false),
            @ApiImplicitParam(dataType = "int",name="targetUserId",value="回复对象id",required = true),
            @ApiImplicitParam(dataType = "int",name="commentId",value="评论id",required = true),
            @ApiImplicitParam(dataType = "String",name="targetNickName",value="回复对象昵称",required = true),
    })
    @RequestMapping(value = "/sendAnswer", method = RequestMethod.POST)
    public Result<String> Answer(@RequestParam("postId") int postId,@RequestParam("answerContent") String answerContent,@RequestParam("nickName") String nickName,
                                  @RequestParam("answerUserId") int answerUserId,@RequestParam("targetAnswerId") int targetAnswerId,
                                 @RequestParam("targetUserId") int targetUserId,@RequestParam("commentId") int commentId,@RequestParam("targetNickName") String targetNickName){
        Answer answer = new Answer();
        answer.setCreateTime(new Date());
        iAnswerService.newAnswer(postId,answerContent,nickName,answerUserId,targetAnswerId,targetUserId,commentId,answer.getCreateTime(),targetNickName);
        String result="回复回执";
        return new ResultUtil<String>().setData(result);
    }


    @ApiOperation(value="回复评论信息")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "long",name="userid",value="用户id",required = true),
    })
    @RequestMapping(value = "/getAnswerToComment", method = RequestMethod.POST)
    public Result<List> getAnswerToComment(@RequestParam("userid") long userid) {
        List<Answer> answer = iAnswerService.getInfo(userid,2);
        List result = new ArrayList();

        for(int i = 0;i<answer.size();i++){
            String time = String.valueOf(answer.get(i).getCreateTime());
            result.add(0,time);
            result.add(answer.get(i));
            String postId = answer.get(i).getTargetPostId();
            result.add(iAnswerService.getPost(postId).get(i));
            String commentId=answer.get(i).getCommentId();
            String answerUserid=answer.get(i).getAnswerUserId();
            result.add(iAnswerService.getUserInfo(answerUserid).get(i));
            result.add(iAnswerService.getCommentInfo(commentId).get(i));
        }
        return new ResultUtil<List>().setData(result);
    }

    /**
     * 此接口返回消息页评论点赞信息
     * @return
     */
    @ApiOperation(value="回复回复")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "long",name="userid",value="用户id",required = true),
    })
    @RequestMapping(value = "/getAnswerToAnswer", method = RequestMethod.POST)
    public Result<List> getAnswerToAnswer(@RequestParam("userid") long userid) {
        List<Answer> answer = iAnswerService.getInfo(userid,1);
        List result = new ArrayList();

        for(int i = 0;i<answer.size();i++){
            String time = String.valueOf(answer.get(i).getCreateTime());
            result.add(0,time);
            result.add(answer.get(i));
            String postId = answer.get(i).getTargetPostId();
            result.add(iAnswerService.getPost(postId).get(i));
            String answerId=answer.get(i).getTargetAnswerId();
            String answerUserid=answer.get(i).getAnswerUserId();
            result.add(iAnswerService.getUserInfo(answerUserid).get(i));
            result.add(iAnswerService.getAnswerInfo(answerId).get(i));
        }
        return new ResultUtil<List>().setData(result);
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "通过id获取")
    public Result<Answer> get(@PathVariable long id) {

        Answer answer = iAnswerService.getById(id);
        return new ResultUtil<Answer>().setData(answer);
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
