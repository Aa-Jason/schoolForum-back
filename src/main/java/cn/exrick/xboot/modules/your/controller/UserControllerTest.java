package cn.exrick.xboot.modules.your.controller;


import cn.exrick.xboot.modules.your.service.UserServiceTest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/user",method = RequestMethod.POST)
@Api(value = "提供用户的登录和注册接口",tags = "用户管理")
@CrossOrigin(origins = "*",maxAge = 3600)
public class UserControllerTest {

    @Resource
    private UserServiceTest userServiceTest;


    @ApiOperation("用户登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "string",name = "username", value = "用户登录账号",required = true),
            @ApiImplicitParam(dataType = "string",name = "password", value = "用户登录密码",required = true)
    })
    @GetMapping("/login")
    public List login(@RequestParam("username") String name,
                      @RequestParam(value = "password") String pwd){
        String resultVO = userServiceTest.checkLogin(name, pwd);
        String username = name;
        String userid = "testid";

        List list = new ArrayList();
        list.add(resultVO);
        list.add(username);
        if(resultVO == "ok"){
            list.add(userid);
        }

        return list;
    }

}
