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

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "通过id获取")
    public Result<List<Post>> get(@PathVariable String id) {

        List<Post> posts = iPostService.selectAllPostById(id);
        return new ResultUtil<List<Post>>().setData(posts);
    }
    //主界面获取的全部数据
    @RequestMapping(value = "/getMainAll", method = RequestMethod.GET)
    @ApiOperation(value = "获取全部主界面数据")
    public Result<List<PostVO>> selectPostInnerId() {

        List<PostVO> postVOS = iPostService.selectPostInnerId();
        return new ResultUtil<List<PostVO>>().setData(postVOS);
    }
    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "获取全部数据")
    public Result<List<Post>> getAll() {

        List<Post> list = iPostService.selectAllPost();
        return new ResultUtil<List<Post>>().setData(list);
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "分页获取")
    public Result<IPage<Post>> getByPage(PageVo page) {

        IPage<Post> data = iPostService.page(PageUtil.initMpPage(page));
        return new ResultUtil<IPage<Post>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "编辑或更新数据")
    public Result<Post> saveOrUpdate(Post post) {

        if (iPostService.saveOrUpdate(post)) {
            return new ResultUtil<Post>().setData(post);
        }
        return new ResultUtil<Post>().setErrorMsg("操作失败");
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "批量通过id删除")
    public Result delAllByIds(@RequestParam long[] ids) {

        for (long id : ids) {
            iPostService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }
}
