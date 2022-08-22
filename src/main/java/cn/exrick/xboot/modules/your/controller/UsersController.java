package cn.exrick.xboot.modules.your.controller;

import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.modules.your.entity.Users;
import cn.exrick.xboot.modules.your.service.IUsersService;
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
@Api(tags = "用户信息表管理接口")
@RequestMapping("/xboot/users")
@Transactional
public class UsersController {

    @Autowired
    private IUsersService iUsersService;

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "通过id获取")
    public Result<Users> get(@PathVariable long id) {

        Users users = iUsersService.getById(id);
        return new ResultUtil<Users>().setData(users);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "获取全部数据")
    public Result<List<Users>> getAll() {

        List<Users> list = iUsersService.list();
        return new ResultUtil<List<Users>>().setData(list);
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "分页获取")
    public Result<IPage<Users>> getByPage(PageVo page) {

        IPage<Users> data = iUsersService.page(PageUtil.initMpPage(page));
        return new ResultUtil<IPage<Users>>().setData(data);
    }

    @RequestMapping(value = "/insertOrUpdate", method = RequestMethod.POST)
    @ApiOperation(value = "编辑或更新数据")
    public Result<Users> saveOrUpdate(Users users) {

        if (iUsersService.saveOrUpdate(users)) {
            return new ResultUtil<Users>().setData(users);
        }
        return new ResultUtil<Users>().setErrorMsg("操作失败");
    }

    @RequestMapping(value = "/delByIds", method = RequestMethod.POST)
    @ApiOperation(value = "批量通过id删除")
    public Result delAllByIds(@RequestParam long[] ids) {

        for (long id : ids) {
            iUsersService.removeById(id);
        }
        return ResultUtil.success("批量通过id删除数据成功");
    }
}
