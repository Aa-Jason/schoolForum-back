package cn.exrick.xboot.common.constant;

/**
 * 常量
 * @author Exrickx
 */
public interface MessageConstant {

    /**
     * 消息发送范围 所有
     */
    Integer MESSAGE_RANGE_ALL = 0;

    /**
     * 消息发送范围指定用户
     */
    Integer MESSAGE_RANGE_USER = 1;

    /**
     * 未读
     */
    Integer MESSAGE_STATUS_UNREAD = 0;

    /**
     * 已读
     */
    Integer MESSAGE_STATUS_READ = 1;

    /**
     * 短信发送范围 所有
     */
    Integer SMS_RANGE_ALL = 0;

    /**
     * 短信发送范围 已注册
     */
    Integer SMS_RANGE_REG = 1;

    /**
     * 短信发送范围 未注册
     */
    Integer SMS_RANGE_UNREG = 2;
}
