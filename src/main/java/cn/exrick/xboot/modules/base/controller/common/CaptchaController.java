package cn.exrick.xboot.modules.base.controller.common;

import cn.exrick.xboot.common.annotation.RateLimiter;
import cn.exrick.xboot.common.constant.CommonConstant;
import cn.exrick.xboot.common.constant.MessageConstant;
import cn.exrick.xboot.common.constant.SettingConstant;
import cn.exrick.xboot.common.redis.RedisTemplateHelper;
import cn.exrick.xboot.common.sms.SmsUtil;
import cn.exrick.xboot.common.utils.CommonUtil;
import cn.exrick.xboot.common.utils.CreateVerifyCode;
import cn.exrick.xboot.common.utils.IpInfoUtil;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.modules.base.service.UserService;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Exrickx
 */
@Api(tags = "验证码接口")
@RequestMapping("/xboot/common/captcha")
@RestController
@Transactional
@Slf4j
public class CaptchaController {

    @Autowired
    private SmsUtil smsUtil;

    @Autowired
    private RedisTemplateHelper redisTemplate;

    @Autowired
    private IpInfoUtil ipInfoUtil;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/init", method = RequestMethod.GET)
    @ApiOperation(value = "初始化验证码")
    @RateLimiter(rate = 1, ipLimit = true)
    public Result initCaptcha(@ApiParam("仅生成数字") @RequestParam(required = false, defaultValue = "false") Boolean isDigit,
                                      @ApiParam("验证码长度") @RequestParam(required = false, defaultValue = "4") Integer length) {

        String captchaId = IdUtil.simpleUUID();
        String code;
        if (isDigit) {
            code = new CreateVerifyCode().randomDigit(length);
        } else {
            code = new CreateVerifyCode().randomStr(length);
        }
        // 缓存验证码
        redisTemplate.set(captchaId, code, 2L, TimeUnit.MINUTES);
        return ResultUtil.data(captchaId);
    }

    @RequestMapping(value = "/draw/{captchaId}", method = RequestMethod.GET)
    @ApiOperation(value = "根据验证码ID获取图片")
    public void drawCaptcha(@PathVariable("captchaId") String captchaId,
                            HttpServletResponse response) throws IOException {

        // 得到验证码 生成指定验证码
        String code = redisTemplate.get(captchaId);
        CreateVerifyCode vCode = new CreateVerifyCode(116, 36, 4, 10, code);
        response.setContentType("image/png");
        vCode.write(response.getOutputStream());
    }

    @RequestMapping(value = "/sendRegistSms/{mobile}", method = RequestMethod.GET)
    @ApiOperation(value = "发送注册短信验证码")
    public Result sendRegistSmsCode(@PathVariable String mobile, HttpServletRequest request) {

        return sendSms(mobile, MessageConstant.SMS_RANGE_UNREG, SettingConstant.SMS_TYPE.SMS_COMMON.name(), request);
    }

    @RequestMapping(value = "/sendLoginSms/{mobile}", method = RequestMethod.GET)
    @ApiOperation(value = "发送登录短信验证码")
    @RateLimiter(name = "sendLoginSms", rate = 1, ipLimit = true)
    public Result sendLoginSmsCode(@PathVariable String mobile, HttpServletRequest request) {

        return sendSms(mobile, MessageConstant.SMS_RANGE_REG, SettingConstant.SMS_TYPE.SMS_COMMON.name(), request);
    }

    @RequestMapping(value = "/sendResetSms/{mobile}", method = RequestMethod.GET)
    @ApiOperation(value = "发送重置密码短信验证码")
    public Result sendResetSmsCode(@PathVariable String mobile, HttpServletRequest request) {

        return sendSms(mobile, MessageConstant.SMS_RANGE_REG, SettingConstant.SMS_TYPE.SMS_RESET_PASS.name(), request);
    }

    @RequestMapping(value = "/sendEditMobileSms/{mobile}", method = RequestMethod.GET)
    @ApiOperation(value = "发送修改手机短信验证码")
    public Result sendEditMobileSmsCode(@PathVariable String mobile, HttpServletRequest request) {

        if (userService.findByMobile(mobile) != null) {
            return ResultUtil.error("该手机号已绑定账户");
        }
        return sendSms(mobile, MessageConstant.SMS_RANGE_ALL, SettingConstant.SMS_TYPE.SMS_COMMON.name(), request);
    }

    /**
     * @param mobile       手机号
     * @param range        发送范围 0发送给所有手机号 1只发送给注册手机 2只发送给未注册手机
     * @param templateType 短信模版类型 详见SettingConstant
     */
    public Result sendSms(String mobile, Integer range, String templateType, HttpServletRequest request) {

        if (MessageConstant.SMS_RANGE_REG.equals(range) && userService.findByMobile(mobile) == null) {
            return ResultUtil.error("手机号未注册");
        } else if (MessageConstant.SMS_RANGE_UNREG.equals(range) && userService.findByMobile(mobile) != null) {
            return ResultUtil.error("手机号已注册");
        }
        // IP限流 1分钟限1个请求
        String key = "sendSms:" + ipInfoUtil.getIpAddr(request);
        String value = redisTemplate.get(key);
        if (StrUtil.isNotBlank(value)) {
            return ResultUtil.error("您发送的太频繁啦，请稍后再试");
        }
        // 生成6位数验证码
        String code = CommonUtil.getRandomNum();
        // 缓存验证码
        redisTemplate.set(CommonConstant.PRE_SMS + mobile, code, 5L, TimeUnit.MINUTES);
        // 发送验证码
        smsUtil.sendCode(mobile, code, templateType);
        // 请求成功 标记限流
        redisTemplate.set(key, "sended", 1L, TimeUnit.MINUTES);
        return ResultUtil.success("发送短信验证码成功");
    }
}
