package cn.exrick.xboot.common.utils;

import cn.exrick.xboot.common.constant.SettingConstant;
import cn.exrick.xboot.common.exception.XbootException;
import cn.exrick.xboot.modules.base.entity.Setting;
import cn.exrick.xboot.modules.base.service.SettingService;
import cn.exrick.xboot.modules.base.vo.EmailSetting;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;

/**
 * @author Exrickx
 */
@Component
@Slf4j
public class EmailUtil {

    @Autowired
    private SettingService settingService;

    @Autowired
    private TemplateEngine templateEngine;

    public EmailSetting getEmailSetting() {

        Setting setting = settingService.get(SettingConstant.EMAIL_SETTING);
        if (StrUtil.isBlank(setting.getValue())) {
            throw new XbootException("您还未配置邮件发送相关配置");
        }
        return new Gson().fromJson(setting.getValue(), EmailSetting.class);
    }

    /**
     * 异步发送邮件
     * @param sendTo       接收者邮箱
     * @param title        邮件标题
     * @param templateName 邮件模板
     * @param variables            模板替换变量Map
     */
    @Async
    public void sendTemplateEmailByMap(String sendTo, String title, String templateName, Map<String, Object> variables) {

        sendTemplateEmail(sendTo, title, templateName, variables, null);
    }

    /**
     * 异步发送邮件
     * @param sendTo       接收者邮箱
     * @param title        邮件标题
     * @param templateName 邮件模板
     * @param o            模板替换对象
     */
    @Async
    public void sendTemplateEmail(String sendTo, String title, String templateName, Object o) {

        sendTemplateEmail(sendTo, title, templateName, null, o);
    }

    private void sendTemplateEmail(String sendTo, String title, String templateName, Map<String, Object> variables, Object o) {

        EmailSetting es = getEmailSetting();

        JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();

        // 设定邮箱服务器配置
        senderImpl.setHost(es.getHost());
        senderImpl.setUsername(es.getUsername());
        senderImpl.setPassword(es.getPassword());
        Properties prop = new Properties();

        // 服务器进行认证
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.timeout", "20000");
        // 邮箱发送服务器端口,这里设置为465端口
        prop.setProperty("mail.smtp.port", "465");
        prop.setProperty("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.ssl.enable", "true");
        senderImpl.setJavaMailProperties(prop);

        // 发送html邮件
        MimeMessage mailMessage = senderImpl.createMimeMessage();
        MimeMessageHelper messageHelper = null;
        // 设置邮件内容
        try {
            messageHelper = new MimeMessageHelper(mailMessage, true, "utf-8");

            messageHelper.setTo(sendTo);
            messageHelper.setFrom(es.getUsername());
            messageHelper.setSubject(title);
            Context context = new Context();
            if (o != null) {
                context.setVariables(ObjectUtil.beanToMap(o));
            } else {
                context.setVariables(variables);
            }
            // 获取模板html代码
            String content = templateEngine.process(templateName, context);
            // true表示HTML格式的邮件
            messageHelper.setText(content, true);
            // 发送邮件
            senderImpl.send(mailMessage);
        } catch (Exception e) {
            log.error(e.toString());
            throw new XbootException("发送邮件失败，请检查邮件配置");
        }
    }
}
