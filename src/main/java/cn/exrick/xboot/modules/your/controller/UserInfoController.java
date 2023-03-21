package cn.exrick.xboot.modules.your.controller;

import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.modules.your.entity.UserInfo;
import cn.exrick.xboot.modules.your.service.IUserInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xqp
 */
@Slf4j
@RestController
@Api(tags = "用户信息表管理接口")
@RequestMapping("/xboot/userInfo")
@Transactional
@CrossOrigin(origins = "*",maxAge = 3600)
public class UserInfoController {

    @Autowired
    private IUserInfoService iUserInfoService;

    @Resource
    private CacheManager cacheManager;

    //验证账号和密码
    public Result<List<UserInfo>> get(@PathVariable String name,@PathVariable  String pwd) {

        List<UserInfo> login = iUserInfoService.loginAuth(name,pwd);
        return new ResultUtil<List<UserInfo>>().setData(login);
    }

    //用账号获取用户id
    @Cacheable(cacheNames = "username",key = "#name",unless = "#result == null")
    public Result<List<UserInfo>> get(@PathVariable String name) {

        List<UserInfo> userid =  iUserInfoService.getUserIdByUsername(name);
        return new ResultUtil<List<UserInfo>>().setData(userid);
    }

    @ApiOperation(value="登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "string",name="username",value="用户登录账号",required = true),
            @ApiImplicitParam(dataType = "string",name="password",value="用户登录密码",required = true),
    })
    @GetMapping(value="/login")
    public Result<List<UserInfo>> login(@RequestParam("username") String name,
                      @RequestParam(value = "password") String pwd){
        List<UserInfo> result0 = iUserInfoService.loginAuth(name,pwd);
        String username= result0.get(0).getUsername();
        return new ResultUtil<List<UserInfo>>().setData(iUserInfoService.getUserIdByUsername(username)) ;
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "通过id获取")
    @Cacheable(cacheNames = "username",key = "#name",unless = "#result == null")
    public Result<UserInfo> get(@PathVariable long id) {

        UserInfo userInfo = iUserInfoService.getById(id);
        return new ResultUtil<UserInfo>().setData(userInfo);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "获取全部用户信息")
    public Result<List<UserInfo>> getAll() {

        List<UserInfo> list = iUserInfoService.list();
        return new ResultUtil<List<UserInfo>>().setData(list);
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "分页获取")
    public Result<IPage<UserInfo>> getByPage(PageVo page) {

        IPage<UserInfo> data = iUserInfoService.page(PageUtil.initMpPage(page));
        return new ResultUtil<IPage<UserInfo>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "编辑或更新数据")
    public Result<UserInfo> saveOrUpdate(UserInfo userInfo) {

        if (iUserInfoService.saveOrUpdate(userInfo)) {
            return new ResultUtil<UserInfo>().setData(userInfo);
        }
        return new ResultUtil<UserInfo>().setErrorMsg("操作失败");
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "批量通过id删除")
    public Result delAllByIds(@RequestParam long[] ids) {

        for (long id : ids) {
            iUserInfoService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }
}
