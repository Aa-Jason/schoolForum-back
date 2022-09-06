package cn.exrick.xboot.modules.your.controller;

import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.modules.your.entity.Post;
import cn.exrick.xboot.modules.your.service.IPostService;
import cn.exrick.xboot.modules.your.vo.PostVO;
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

import java.util.List;

/**
 * @author xqp
 */
@Slf4j
@RestController
@Api(tags = "帖子信息表管理接口")
@RequestMapping("/xboot/post")
@Transactional
@CrossOrigin
public class PostController {

    @Autowired
    private IPostService iPostService;


    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "int", name = "id", value = "id", required = true),
            @ApiImplicitParam(dataType = "int", name = "dif", value = "区分", required = true),
    })
    @RequestMapping(value = "/getPostById", method = RequestMethod.POST)
    @ApiOperation(value = "通过用户id或者帖子id获取帖子")
    public Result<List<Post>> getPostById(@RequestParam("id") int id, @RequestParam("dif") int dif) {//用dif区分用户和帖子，1代表用户，2代表帖子
        System.out.println("接收到的帖子ID:" + id);
        List<Post> posts = iPostService.selectAllPostById(id, dif);
        return new ResultUtil<List<Post>>().setData(posts);
    }


    //主界面获取的全部数据
    @RequestMapping(value = "/getPostInfo", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "page", value = "页数", required = true),
            @ApiImplicitParam(dataType = "String", name = "part", value = "分区", required = true),
    })
    @ApiOperation(value = "分页按分区获取帖子数据")
    public Result<List<PostVO>> selectPostInnerId(@RequestParam("page") String page, @RequestParam("part") String part) {
        int num = 10 * Integer.parseInt(page);
        List<PostVO> postVOS = iPostService.selectPostInnerId(num, Integer.parseInt(part));

        return new ResultUtil<List<PostVO>>().setData(postVOS);
    }

    //根据输入关键字进行搜索
    @RequestMapping(value = "/getPostByKey", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "key", value = "关键字", required = true),
    })
    @ApiOperation(value = "根据输入关键字进行搜索")
    public Result<List<PostVO>> selectPostByKey(@RequestParam("key") String key) {
        List<PostVO> postVOS = iPostService.selectPostByKey(key);
        return new ResultUtil<List<PostVO>>().setData(postVOS);
    }

    //用户发帖
    @RequestMapping(value = "/putPosted", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "postContent", value = "帖子内容", required = true),
            @ApiImplicitParam(dataType = "String", name = "postPart", value = "帖子分类", required = true),
            @ApiImplicitParam(dataType = "String", name = "postPicture", value = "发帖图片", required = false),
            @ApiImplicitParam(dataType = "String", name = "postTitle", value = "帖子标题", required = true),
            @ApiImplicitParam(dataType = "int", name = "userId", value = "用户ID", required = true),
            @ApiImplicitParam(dataType = "String", name = "nickName", value = "用户昵称", required = true),
    })
    @ApiOperation(value = "用户发帖")
    public Result<List<Post>> putPosted(@RequestParam("postContent") String postContent,
                                        @RequestParam("postPart") String postPart,
                                        @RequestParam("postPicture") String postPicture,
                                        @RequestParam("postTitle") String postTitle,
                                        @RequestParam("userId") int userId,
                                        @RequestParam("nickName") String nickName) {
        //List<Post> posted = iPostService.posted(post.getPostContent(),post.getPostPart(),post.getPostPicture(),post.getPostTitle());

        List<Post> posted = iPostService.posted(postContent, postPart, postPicture, postTitle, userId, nickName);
        if (posted.size() > 0) {
            return new ResultUtil<List<Post>>().setData(posted);
        } else {
            return new ResultUtil<List<Post>>().setErrorMsg("发帖失败");
        }

    }

    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "int", name = "id", value = "id", required = true),
    })
    @RequestMapping(value = "/delPostById", method = RequestMethod.POST)
    @ApiOperation(value = "通过id删除帖子")
    public Result<String> delPostById(@RequestParam("id") int id) {
        System.out.println("接收到的帖子ID:" + id);
        iPostService.delPostById(id);
        String result = "删除回执";
        return new ResultUtil<String>().setData(result);
    }


    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "获取全部数据")
    public Result<List<Post>> getAll() {

        List<Post> list = iPostService.selectAllPost();
        return new ResultUtil<List<Post>>().setData(list);
    }

//    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
//    @ApiOperation(value = "分页获取")
//    public Result<IPage<Post>> getByPage(PageVo page) {
//
//        IPage<Post> data = iPostService.page(PageUtil.initMpPage(page));
//        return new ResultUtil<IPage<Post>>().setData(data);
//    }
//
//    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
//    @ApiOperation(value = "编辑或更新数据")
//    public Result<Post> saveOrUpdate(Post post) {
//
//        if (iPostService.saveOrUpdate(post)) {
//            return new ResultUtil<Post>().setData(post);
//        }
//        return new ResultUtil<Post>().setErrorMsg("操作失败");
//    }
//
//    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
//    @ApiOperation(value = "批量通过id删除")
//    public Result delAllByIds(@RequestParam long[] ids) {
//
//        for (long id : ids) {
//            iPostService.removeById(id);
//        }
//        return ResultUtil.success("批量通过id删除数据成功");
//    }
}
