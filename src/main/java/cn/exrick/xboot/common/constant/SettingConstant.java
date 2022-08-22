package cn.exrick.xboot.common.constant;

/**
 * @author Exrickx
 */
public interface SettingConstant {

    /**
     * 当前使用OSS
     */
    String OSS_USED = "OSS_USED";

    /**
     * 七牛OSS配置
     */
    String QINIU_OSS = "QINIU_OSS";

    /**
     * 七牛云存储区域 自动判断
     */
    Integer ZONE_AUTO = -1;

    /**
     * 七牛云存储区域 华东
     */
    Integer ZONE_ZERO = 0;

    /**
     * 七牛云存储区域 华北
     */
    Integer ZONE_ONE = 1;

    /**
     * 七牛云存储区域 华南
     */
    Integer ZONE_TWO = 2;

    /**
     * 七牛云存储区域 北美
     */
    Integer ZONE_THREE = 3;

    /**
     * 七牛云存储区域 东南亚
     */
    Integer ZONE_FOUR = 4;

    /**
     * 阿里OSS配置
     */
    String ALI_OSS = "ALI_OSS";

    /**
     * 腾讯COS配置
     */
    String TENCENT_OSS = "TENCENT_OSS";

    /**
     * 本地OSS配置
     */
    String LOCAL_OSS = "LOCAL_OSS";

    /**
     * Minio配置
     */
    String MINIO_OSS = "MINIO_OSS";

    /**
     * 当前使用短信
     */
    String SMS_USED = "SMS_USED";

    /**
     * 阿里短信配置
     */
    String ALI_SMS = "ALI_SMS";

    /**
     * 腾讯云短信配置
     */
    String TENCENT_SMS = "TENCENT_SMS";

    /**
     * 短信模版类型
     */
    enum SMS_TYPE {
        // 通用
        SMS_COMMON,
        // 登录验证码
        SMS_LOGIN,
        // 注册验证码
        SMS_REGISTER,
        // 修改手机
        SMS_CHANGE_MOBILE,
        // 修改密码
        SMS_CHANGE_PASS,
        // 重置密码
        SMS_RESET_PASS,
        // 工作流消息
        SMS_ACTIVITI
    }

    /**
     * 邮箱配置
     */
    String EMAIL_SETTING = "EMAIL_SETTING";

    /**
     * 其他配置
     */
    String OTHER_SETTING = "OTHER_SETTING";

    /**
     * 机器人配置
     */
    String CHAT_SETTING = "CHAT_SETTING";

    /**
     * 公告配置
     */
    String NOTICE_SETTING = "NOTICE_SETTING";
}
