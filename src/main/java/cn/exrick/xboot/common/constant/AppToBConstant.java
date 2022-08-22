package cn.exrick.xboot.common.constant;

/**
 * @author Exrickx
 */
public interface AppToBConstant {

    /**
     * 初始生成二维码状态
     */
    String SCAN_LOGIN_STATUS_INIT = "0";

    /**
     * 被用户扫码
     */
    String SCAN_LOGIN_STATUS_SCANNED = "1";

    /**
     * 用户确认登录
     */
    String SCAN_LOGIN_STATUS_SUCCESS = "2";

    /**
     * 用户取消登录
     */
    String SCAN_LOGIN_STATUS_CANCEL = "-1";

    /**
     * 二维码过期
     */
    String SCAN_LOGIN_STATUS_EXPIRED = "-2";

    /**
     * 扫码登录
     */
    String SCAN_LOGIN_SCHEME = "xboot://scanQRLogin";
}
