package cn.exrick.xboot.modules.activiti.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Exrickx
 */
@Data
public class ActMessageVo {

    @ApiModelProperty(value = "是否发送站内消息")
    Boolean sendMessage = false;

    @ApiModelProperty(value = "是否发送短信通知")
    Boolean sendSms = false;

    @ApiModelProperty(value = "是否发送邮件通知")
    Boolean sendEmail = false;
}
