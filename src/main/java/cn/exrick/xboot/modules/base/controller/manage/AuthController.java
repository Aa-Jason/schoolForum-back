package cn.exrick.xboot.modules.base.controller.manage;

import cn.exrick.xboot.common.annotation.RateLimiter;
import cn.exrick.xboot.common.annotation.SystemLog;
import cn.exrick.xboot.common.constant.AppToBConstant;
import cn.exrick.xboot.common.constant.UserConstant;
import cn.exrick.xboot.common.enums.LogType;
import cn.exrick.xboot.common.exception.XbootException;
import cn.exrick.xboot.common.redis.RedisTemplateHelper;
import cn.exrick.xboot.common.utils.NameUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.utils.SecurityUtil;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.config.properties.XbootTokenProperties;
import cn.exrick.xboot.modules.base.async.AddMessage;
import cn.exrick.xboot.modules.base.entity.Role;
import cn.exrick.xboot.modules.base.entity.User;
import cn.exrick.xboot.modules.base.entity.UserRole;
import cn.exrick.xboot.modules.base.service.RoleService;
import cn.exrick.xboot.modules.base.service.UserRoleService;
import cn.exrick.xboot.modules.base.service.UserService;
import cn.exrick.xboot.modules.base.vo.QRStatusVo;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author Exrickx
 */
@Slf4j
@RestController
@Api(tags = "用户认证相关接口")
@RequestMapping("/xboot/auth")
@CacheConfig(cacheNames = "user")
@Transactional
public class AuthController {

    public static final String USER = "user::";

    public static final String LOGIN_FAIL_FLAG = "LOGIN_FAIL_FLAG:";

    public static final String LOGIN_TIME_LIMIT = "LOGIN_TIME_LIMIT:";

    public static final String GET_NEW_TOKEN_PRE = "GET_NEW_TOKEN:";

    public static final Integer LOGIN_FAIL_TIP_TIME = 3;

    @Autowired
    private XbootTokenProperties tokenProperties;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private AddMessage addMessage;

    @Autowired
    private RedisTemplateHelper redisTemplate;

    @Autowired
    private SecurityUtil securityUtil;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @SystemLog(description = "账号登录", type = LogType.LOGIN)
    @ApiOperation(value = "账号/手机/邮箱登录")
    public Result login(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam(required = false) Boolean saveLogin) {

        String loginFailKey = LOGIN_FAIL_FLAG + username;
        String loginTimeKey = LOGIN_TIME_LIMIT + username;

        String valueFailFlag = redisTemplate.get(loginFailKey);
        Long timeRest = redisTemplate.getExpire(loginFailKey, TimeUnit.MINUTES);
        if (StrUtil.isNotBlank(valueFailFlag)) {
            // 超过限制次数
            return ResultUtil.error("登录错误次数超过限制，请" + timeRest + "分钟后再试");
        }

        User user = securityUtil.checkUserPassword(username, password);
        if (user == null) {
            // 记录密码错误次数
            String valueTime = redisTemplate.get(loginTimeKey);
            if (StrUtil.isBlank(valueTime)) {
                valueTime = "0";
            }
            // 获取已登录错误次数
            Integer loginFailTime = Integer.parseInt(valueTime) + 1;
            redisTemplate.set(loginTimeKey, loginFailTime.toString(), tokenProperties.getLoginAfterTime(), TimeUnit.MINUTES);
            if (loginFailTime >= tokenProperties.getLoginTimeLimit()) {
                redisTemplate.set(loginFailKey, "FAIL", tokenProperties.getLoginAfterTime(), TimeUnit.MINUTES);
            }
            int restLoginTime = tokenProperties.getLoginTimeLimit() - loginFailTime;
            if (restLoginTime > 0 && restLoginTime <= LOGIN_FAIL_TIP_TIME) {
                return ResultUtil.error("账号或密码错误，还有" + restLoginTime + "次尝试机会");
            } else if (restLoginTime <= 0) {
                return ResultUtil.error("登录错误次数超过限制，请" + tokenProperties.getLoginAfterTime() + "分钟后再试");
            }
            return ResultUtil.error("账号或密码错误");
        }

        String accessToken = securityUtil.getToken(user, saveLogin);
        return ResultUtil.data(accessToken);
    }

    @RequestMapping(value = "/smsLogin", method = RequestMethod.POST)
    @SystemLog(description = "短信登录", type = LogType.LOGIN)
    @ApiOperation(value = "短信登录")
    public Result smsLogin(@RequestParam String mobile,
                                   @RequestParam(required = false) Boolean saveLogin) {

        User user = userService.findByMobile(mobile);
        if (user == null) {
            throw new XbootException("手机号不存在");
        }
        String accessToken = securityUtil.getToken(user, saveLogin);
        return ResultUtil.data(accessToken);
    }

    @RequestMapping(value = "/getLoginQRCode", method = RequestMethod.GET)
    @ApiOperation(value = "获取扫码登录二维码")
    @RateLimiter(rate = 1, ipLimit = true)
    public Result getLoginQRCode() {

        String checkToken = IdUtil.simpleUUID();
        redisTemplate.set(checkToken, AppToBConstant.SCAN_LOGIN_STATUS_INIT, 2L, TimeUnit.MINUTES);
        String scheme = AppToBConstant.SCAN_LOGIN_SCHEME + "?checkToken=" + checkToken;
        return ResultUtil.data(scheme);
    }

    @RequestMapping(value = "/checkQRStatus/{checkToken}", method = RequestMethod.GET)
    @ApiOperation(value = "检查二维状态码")
    public Result checkQRStatus(@PathVariable String checkToken) {

        String state = redisTemplate.get(checkToken);
        QRStatusVo statusVo = new QRStatusVo();
        if (StrUtil.isBlank(state)) {
            statusVo.setStatus(AppToBConstant.SCAN_LOGIN_STATUS_EXPIRED);
            return ResultUtil.data(statusVo);
        }

        if (AppToBConstant.SCAN_LOGIN_STATUS_SUCCESS.equals(state)) {
            String accessToken = redisTemplate.get(GET_NEW_TOKEN_PRE + checkToken);
            if (StrUtil.isBlank(accessToken)) {
                statusVo.setStatus(AppToBConstant.SCAN_LOGIN_STATUS_EXPIRED);
                return ResultUtil.data(statusVo);
            }
            statusVo.setAccessToken(accessToken);
        }
        statusVo.setStatus(state);
        return ResultUtil.data(statusVo);
    }

    @RequestMapping(value = "/scanLogin", method = RequestMethod.POST)
    @SystemLog(description = "扫码登录", type = LogType.LOGIN)
    @ApiOperation(value = "扫码登录")
    public Result scanLogin(@RequestParam String checkToken,
                                    @RequestParam(required = false, defaultValue = "false") Boolean cancel,
                                    @RequestParam(required = false) String accessToken) {

        String state = redisTemplate.get(checkToken);
        if (StrUtil.isBlank(state)) {
            return ResultUtil.error("无效的二维码或已过期");
        }
        if (AppToBConstant.SCAN_LOGIN_STATUS_INIT.equals(state)) {
            redisTemplate.set(checkToken, AppToBConstant.SCAN_LOGIN_STATUS_SCANNED, 2L, TimeUnit.MINUTES);
            return ResultUtil.data(AppToBConstant.SCAN_LOGIN_STATUS_SCANNED);
        } else if (AppToBConstant.SCAN_LOGIN_STATUS_SCANNED.equals(state)) {
            // 已扫码
            if (cancel) {
                // 取消
                redisTemplate.set(checkToken, AppToBConstant.SCAN_LOGIN_STATUS_CANCEL, 2L, TimeUnit.MINUTES);
                return ResultUtil.data(AppToBConstant.SCAN_LOGIN_STATUS_CANCEL);
            }
            // 已确认 校验accessToken
            User user = securityUtil.getCurrUser();
            // 颁发新Token
            String newAccessToken = securityUtil.getToken(user, true);
            redisTemplate.set(checkToken, AppToBConstant.SCAN_LOGIN_STATUS_SUCCESS, 2L, TimeUnit.MINUTES);
            redisTemplate.set(GET_NEW_TOKEN_PRE + checkToken, newAccessToken, 2L, TimeUnit.MINUTES);
            return ResultUtil.data(AppToBConstant.SCAN_LOGIN_STATUS_SUCCESS);
        }
        return ResultUtil.error("checkToken无效");
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ApiOperation(value = "注册用户")
    public Result register(@Valid User u) {

        // 校验是否已存在
        NameUtil.checkUserInfo(u);

        // 默认Nickname
        String nickname = u.getMobile().substring(0, 3) + "****" + u.getMobile().substring(7, 11);
        // 加密密码
        String encryptPass = new BCryptPasswordEncoder().encode(u.getPassword());
        u.setPassword(encryptPass).setType(UserConstant.USER_TYPE_NORMAL).setNickname(nickname);

        User user = userService.save(u);

        // 默认角色
        List<Role> roleList = roleService.findByDefaultRole(true);
        if (roleList != null && roleList.size() > 0) {
            for (Role role : roleList) {
                UserRole ur = new UserRole().setUserId(user.getId()).setRoleId(role.getId());
                userRoleService.save(ur);
            }
        }
        // 异步发送创建账号消息
        addMessage.addSendMessage(user.getId());

        return ResultUtil.data(user);
    }

    @RequestMapping(value = "/resetByMobile", method = RequestMethod.POST)
    @ApiOperation(value = "通过短信重置密码")
    public Result resetByMobile(@RequestParam String mobile,
                                        @RequestParam String password,
                                        @RequestParam String passStrength) {

        User u = userService.findByMobile(mobile);
        String encryptPass = new BCryptPasswordEncoder().encode(password);
        u.setPassword(encryptPass).setPassStrength(passStrength);
        userService.update(u);
        // 删除缓存
        redisTemplate.delete(USER + u.getUsername());
        return ResultUtil.success("重置密码成功");
    }

    @RequestMapping(value = "/resetByEmail", method = RequestMethod.POST)
    @ApiOperation(value = "通过邮箱重置密码")
    public Result resetByEmail(@RequestParam String email,
                                       @RequestParam String password,
                                       @RequestParam String passStrength) {

        User u = userService.findByEmail(email);

        // 在线DEMO所需
        if ("test".equals(u.getUsername()) || "test2".equals(u.getUsername())) {
            return ResultUtil.error("演示账号不支持重置密码");
        }

        String encryptPass = new BCryptPasswordEncoder().encode(password);
        u.setPassword(encryptPass);
        u.setPassStrength(passStrength);
        userService.update(u);
        // 删除缓存
        redisTemplate.delete("user::" + u.getUsername());
        return ResultUtil.success("重置密码成功");
    }

}
