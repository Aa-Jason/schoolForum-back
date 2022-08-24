package cn.exrick.xboot.modules.your.controller;

import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.modules.your.entity.Support;
import cn.exrick.xboot.modules.your.entity.UserInfo;
import cn.exrick.xboot.modules.your.service.ISupportService;
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
@Api(tags = "点赞信息管理接口")
@RequestMapping("/xboot/support")
@Transactional
public class SupportController {

    @Autowired
    private ISupportService iSupportService;

    //根据userid查询support表
    public Result<List<Support>> Post(@PathVariable long id){
        List<Support> info = iSupportService.getInfo(id);
        return new ResultUtil<List<Support>>().setData(info);
    }

    //根据postId查询帖子信息
    public Result<List<Support>> Post(@PathVariable String postId){
        List<Support> info = iSupportService.getPost(postId);
        return new ResultUtil<List<Support>>().setData(info);
    }
    //根据userid查询用户信息表
    public Result<List<Support>> get(@PathVariable String userId){
        List<Support> info = iSupportService.getUserInfo(userId);
        return new ResultUtil<List<Support>>().setData(info);
    }
    //根据commentId查询评论信息表
    public Result<List<Support>> getComment(@PathVariable String commentId){
        List<Support> info = iSupportService.getCommentInfo(commentId);
        return new ResultUtil<List<Support>>().setData(info);
    }


    /**
     * 此接口返回消息页帖子点赞信息
     * @return
     */
    @ApiOperation(value="帖子点赞信息接口")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "long",name="userid",value="用户id",required = true),
    })
    @RequestMapping(value = "/getPostSupport", method = RequestMethod.GET)
    public Result<List> getPostSupport(@RequestParam("userid") long userid) {
        List<Support> support = iSupportService.getInfo(userid);
        List result = new ArrayList();
        for(int i = 0;i<support.size();i++){
            String supportUserId = support.get(i).getSupportUserId();
            String postId = support.get(i).getTargetPostId();
            result = iSupportService.getPost(postId);
            System.out.println(iSupportService.getUserInfo(supportUserId).get(i));
            List userInfo = iSupportService.getUserInfo(supportUserId);
            result.add(userInfo.get(i));
        }
        return new ResultUtil<List>().setData(result);
    }

    /**
     * 此接口返回消息页评论点赞信息
     * @return
     */
    @ApiOperation(value="评论点赞信息接口")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "long",name="userid",value="用户id",required = true),
    })
    @RequestMapping(value = "/getCommentSupport", method = RequestMethod.GET)
    public Result<List> getCommentSupport(@RequestParam("userid") long userid) {
        List<Support> sup = iSupportService.getInfo(userid);
        List result = new ArrayList();
        for(int i = 0;i<sup.size();i++){
            String supportUserId = sup.get(i).getSupportUserId();
            String postId = sup.get(i).getTargetPostId();
            result = iSupportService.getPost(postId);
            System.out.println(iSupportService.getUserInfo(supportUserId).get(i));
            List userInfo = iSupportService.getUserInfo(supportUserId);
            result.add(userInfo.get(i));
            String commentId = sup.get(i).getTargetCommentId();
            List commentInfo = iSupportService.getCommentInfo(commentId);
            result.add(commentInfo.get(i));
        }

        return new ResultUtil<List>().setData(result);
    }


    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "通过id获取")
    public Result<Support> get(@PathVariable long id) {

        Support support = iSupportService.getById(id);
        return new ResultUtil<Support>().setData(support);
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
