package cn.exrick.xboot.common.constant;

/**
 * @author exrick
 */
public interface UserConstant {
    /**
     * 用户默认头像
     */
    String USER_DEFAULT_AVATAR = "https://ooo.0o0.ooo/2019/04/28/5cc5a71a6e3b6.png";
    /**
     * 用户正常状态
     */
    Integer USER_STATUS_NORMAL = 0;
    /**
     * 用户禁用状态
     */
    Integer USER_STATUS_LOCK = -1;
    /**
     * 普通用户
     */
    Integer USER_TYPE_NORMAL = 0;
    /**
     * 管理员
     */
    Integer USER_TYPE_ADMIN = 1;
}
