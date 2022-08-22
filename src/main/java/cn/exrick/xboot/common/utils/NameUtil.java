package cn.exrick.xboot.common.utils;

import cn.exrick.xboot.common.exception.XbootException;
import cn.exrick.xboot.modules.base.entity.User;
import cn.exrick.xboot.modules.base.service.UserService;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户名验证工具类
 * @author Exrickx
 */
@Slf4j
public class NameUtil {

    private static UserService userService = SpringUtil.getBean(UserService.class);

    public static final String regUsername = "^[a-zA-Z0-9_]{1,16}$";

    public static final String regMobile = "^[1][3,4,5,6,7,8,9][0-9]{9}$";

    public static final String regEmail = "^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

    /**
     * 由字母、数字、下划线、中文组成，不能超过16位
     */
    private static final Pattern pUsername = Pattern.compile(regUsername);

    /**
     * 普通用户11位手机号
     * 10、11、12开头的号码已分配给特定机构
     */
    private static final Pattern pMobile = Pattern.compile(regMobile);

    /**
     * 邮箱
     * http://emailregex.com/
     */
    private static final Pattern pEmail = Pattern.compile(regEmail);

    public static boolean username(String v) {

        if (StrUtil.isBlank(v)) {
            return false;
        }
        Matcher m = pUsername.matcher(v);
        if (m.matches()) {
            return true;
        }
        return false;
    }

    public static boolean mobile(String v) {

        if (StrUtil.isBlank(v)) {
            return false;
        }
        Matcher m = pMobile.matcher(v);
        if (m.matches()) {
            return true;
        }
        return false;
    }

    public static boolean email(String v) {

        if (StrUtil.isBlank(v)) {
            return false;
        }
        Matcher m = pEmail.matcher(v);
        if (m.matches()) {
            return true;
        }
        return false;
    }

    /**
     * 校验用户名、手机号、邮箱、昵称 不校验传空字符或null
     * @param user
     */
    public static void checkUserInfo(User user) {

        if (user == null) {
            throw new XbootException("user不能为空");
        }

        String username = user.getUsername();
        String mobile = user.getMobile();
        String email = user.getEmail();
        // 由于支持手机密码登录 登录账号不能为他人手机号
        if (NameUtil.mobile(username) && !username.equals(mobile)) {
            throw new XbootException("登录账号不能为他人手机号");
        }

        if (StrUtil.isNotBlank(username) && userService.findByUsername(username) != null) {
            throw new XbootException("该登录账号已被注册");
        }
        if (StrUtil.isNotBlank(email) && userService.findByEmail(email) != null) {
            throw new XbootException("该邮箱已被注册");
        }
        if (StrUtil.isNotBlank(mobile) && userService.findByMobile(mobile) != null) {
            throw new XbootException("该手机号已被注册");
        }
    }
}
