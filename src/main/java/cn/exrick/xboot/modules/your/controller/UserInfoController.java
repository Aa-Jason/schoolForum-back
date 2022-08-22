package cn.exrick.xboot.modules.your.controller;

import cn.exrick.xboot.common.utils.PageUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.modules.your.entity.UserInfo;
import cn.exrick.xboot.modules.your.service.IUserInfoService;
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
@RequestMapping("/xboot/userInfo")
@Transactional
public class UserInfoController {

    @Autowired
    private IUserInfoService iUserInfoService;

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "通过id获取")
    public Result<UserInfo> get(@PathVariable long id) {

        UserInfo userInfo = iUserInfoService.getById(id);
        return new ResultUtil<UserInfo>().setData(userInfo);
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "获取全部数据")
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
