package cn.exrick.xboot.modules.base.controller.manage;

import cn.exrick.xboot.common.constant.SettingConstant;
import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.modules.base.entity.Setting;
import cn.exrick.xboot.modules.base.service.SettingService;
import cn.exrick.xboot.modules.base.vo.*;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Exrickx
 */
@Slf4j
@RestController
@Api(tags = "基本配置接口")
@RequestMapping("/xboot/setting")
public class SettingController {

    @Autowired
    private SettingService settingService;

    @RequestMapping(value = "/seeSecret/{settingName}", method = RequestMethod.GET)
    @ApiOperation(value = "查看私密配置")
    public Result seeSecret(@PathVariable String settingName) {

        String result = "";
        Setting setting = settingService.get(settingName);
        if (setting == null || StrUtil.isBlank(setting.getValue())) {
            return ResultUtil.error("配置不存在");
        }
        if (settingName.equals(SettingConstant.QINIU_OSS) || settingName.equals(SettingConstant.ALI_OSS)
                || settingName.equals(SettingConstant.TENCENT_OSS) || settingName.equals(SettingConstant.MINIO_OSS)) {
            result = new Gson().fromJson(setting.getValue(), OssSetting.class).getSecretKey();
        } else if (settingName.equals(SettingConstant.ALI_SMS) || settingName.equals(SettingConstant.TENCENT_SMS)) {
            result = new Gson().fromJson(setting.getValue(), SmsSetting.class).getSecretKey();
        } else if (settingName.equals(SettingConstant.EMAIL_SETTING)) {
            result = new Gson().fromJson(setting.getValue(), EmailSetting.class).getPassword();
        }
        return ResultUtil.data(result);
    }

    @RequestMapping(value = "/oss/check", method = RequestMethod.GET)
    @ApiOperation(value = "检查OSS配置")
    public Result ossCheck() {

        Setting setting = settingService.get(SettingConstant.OSS_USED);
        if (setting == null || StrUtil.isBlank(setting.getValue())) {
            return ResultUtil.error(501, "您还未配置第三方OSS服务");
        }
        return ResultUtil.data(setting.getValue());
    }

    @RequestMapping(value = "/sms/check", method = RequestMethod.GET)
    @ApiOperation(value = "检查短信配置")
    public Result smsCheck() {

        Setting setting = settingService.get(SettingConstant.SMS_USED);
        if (setting == null || StrUtil.isBlank(setting.getValue())) {
            return ResultUtil.error(501, "您还未配置第三方短信服务");
        }
        return ResultUtil.data(setting.getValue());
    }

    @RequestMapping(value = "/oss/{serviceName}", method = RequestMethod.GET)
    @ApiOperation(value = "查看OSS配置")
    public Result<OssSetting> oss(@PathVariable String serviceName) {

        Setting setting = new Setting();
        if (serviceName.equals(SettingConstant.QINIU_OSS) || serviceName.equals(SettingConstant.ALI_OSS)
                || serviceName.equals(SettingConstant.TENCENT_OSS) || serviceName.equals(SettingConstant.MINIO_OSS)
                || serviceName.equals(SettingConstant.LOCAL_OSS)) {
            setting = settingService.get(serviceName);
        }
        if (setting == null || StrUtil.isBlank(setting.getValue())) {
            return new ResultUtil<OssSetting>().setData(null);
        }
        OssSetting ossSetting = new Gson().fromJson(setting.getValue(), OssSetting.class);
        ossSetting.setSecretKey("**********");
        return new ResultUtil<OssSetting>().setData(ossSetting);
    }

    @RequestMapping(value = "/sms/{serviceName}", method = RequestMethod.GET)
    @ApiOperation(value = "查看短信配置")
    public Result<SmsSetting> sms(@PathVariable String serviceName) {

        Setting setting = new Setting();
        if (serviceName.equals(SettingConstant.ALI_SMS) || serviceName.equals(SettingConstant.TENCENT_SMS)) {
            setting = settingService.get(serviceName);
        }
        if (setting == null || StrUtil.isBlank(setting.getValue())) {
            return new ResultUtil<SmsSetting>().setData(null);
        }
        SmsSetting smsSetting = new Gson().fromJson(setting.getValue(), SmsSetting.class);
        smsSetting.setSecretKey("**********");
        if (smsSetting.getType() != null) {
            Setting code = settingService.get(serviceName + "_" + smsSetting.getType());
            smsSetting.setTemplateCode(code.getValue());
        }
        return new ResultUtil<SmsSetting>().setData(smsSetting);
    }

    @RequestMapping(value = "/sms/templateCodes/{serviceName}", method = RequestMethod.GET)
    @ApiOperation(value = "查看短信模板配置")
    public Result smsTemplateCode(@PathVariable String serviceName) {

        List<Map> list = new ArrayList<>();
        for (SettingConstant.SMS_TYPE SMS_TYPE : SettingConstant.SMS_TYPE.values()) {
            Setting setting = settingService.get(serviceName + "_" + SMS_TYPE.name());
            Map<String, String> map = new HashMap<>();
            String value = "";
            if (StrUtil.isNotBlank(setting.getValue())) {
                value = setting.getValue();
            }
            map.put("type", SMS_TYPE.name());
            map.put("templateCode", value);
            list.add(map);
        }
        return ResultUtil.data(list);
    }

    @RequestMapping(value = "/email", method = RequestMethod.GET)
    @ApiOperation(value = "查看email配置")
    public Result<EmailSetting> email() {

        Setting setting = settingService.get(SettingConstant.EMAIL_SETTING);
        if (setting == null || StrUtil.isBlank(setting.getValue())) {
            return new ResultUtil<EmailSetting>().setData(null);
        }
        EmailSetting emailSetting = new Gson().fromJson(setting.getValue(), EmailSetting.class);
        emailSetting.setPassword("**********");
        return new ResultUtil<EmailSetting>().setData(emailSetting);
    }

    @RequestMapping(value = "/other", method = RequestMethod.GET)
    @ApiOperation(value = "查看其他配置")
    public Result<OtherSetting> other() {

        Setting setting = settingService.get(SettingConstant.OTHER_SETTING);
        if (setting == null || StrUtil.isBlank(setting.getValue())) {
            return new ResultUtil<OtherSetting>().setData(null);
        }
        OtherSetting otherSetting = new Gson().fromJson(setting.getValue(), OtherSetting.class);
        return new ResultUtil<OtherSetting>().setData(otherSetting);
    }

    @RequestMapping(value = "/autoChat", method = RequestMethod.GET)
    @ApiOperation(value = "机器人配置")
    public Result<AutoChatSetting> autoChat() {

        Setting setting = settingService.get(SettingConstant.CHAT_SETTING);
        if (setting == null || StrUtil.isBlank(setting.getValue())) {
            return new ResultUtil<AutoChatSetting>().setData(null);
        }
        AutoChatSetting chatSetting = new Gson().fromJson(setting.getValue(), AutoChatSetting.class);
        return new ResultUtil<AutoChatSetting>().setData(chatSetting);
    }

    @RequestMapping(value = "/notice", method = RequestMethod.GET)
    @ApiOperation(value = "查看公告配置")
    public Result<NoticeSetting> notice() {

        Setting setting = settingService.get(SettingConstant.NOTICE_SETTING);
        if (setting == null || StrUtil.isBlank(setting.getValue())) {
            return new ResultUtil<NoticeSetting>().setData(null);
        }
        NoticeSetting noticeSetting = new Gson().fromJson(setting.getValue(), NoticeSetting.class);
        return new ResultUtil<NoticeSetting>().setData(noticeSetting);
    }

    @RequestMapping(value = "/oss/set", method = RequestMethod.POST)
    @ApiOperation(value = "OSS配置")
    public Result ossSet(OssSetting ossSetting) {

        String name = ossSetting.getServiceName();
        Setting setting = settingService.get(name);
        if (name.equals(SettingConstant.QINIU_OSS) || name.equals(SettingConstant.ALI_OSS)
                || name.equals(SettingConstant.TENCENT_OSS) || name.equals(SettingConstant.MINIO_OSS)) {
            // 判断是否修改secrectKey 保留原secrectKey 避免保存***加密字符
            if (StrUtil.isNotBlank(setting.getValue()) && !ossSetting.getChanged()) {
                String secrectKey = new Gson().fromJson(setting.getValue(), OssSetting.class).getSecretKey();
                ossSetting.setSecretKey(secrectKey);
            }
        }
        setting.setValue(new Gson().toJson(ossSetting));
        settingService.saveOrUpdate(setting);
        // 保存启用的OSS服务商
        Setting used = settingService.get(SettingConstant.OSS_USED);
        used.setValue(name);
        settingService.saveOrUpdate(used);
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/sms/set", method = RequestMethod.POST)
    @ApiOperation(value = "短信配置")
    public Result smsSet(SmsSetting smsSetting) {

        String name = smsSetting.getServiceName();
        Setting setting = settingService.get(name);
        if (name.equals(SettingConstant.ALI_SMS) || name.equals(SettingConstant.TENCENT_SMS)) {
            // 判断是否修改secrectKey 保留原secrectKey 避免保存***加密字符
            if (StrUtil.isNotBlank(setting.getValue()) && !smsSetting.getChanged()) {
                String secrectKey = new Gson().fromJson(setting.getValue(), SmsSetting.class).getSecretKey();
                smsSetting.setSecretKey(secrectKey);
            }
        }
        setting.setValue(new Gson().toJson(smsSetting.setType(null).setTemplateCode(null)));
        settingService.saveOrUpdate(setting);
        // 保存启用的短信服务商
        Setting used = settingService.get(SettingConstant.SMS_USED);
        used.setValue(name);
        settingService.saveOrUpdate(used);
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/sms/templateCode/set", method = RequestMethod.POST)
    @ApiOperation(value = "短信配置")
    public Result smsTemplateCodeSet(SmsSetting sms) {

        if (StrUtil.isNotBlank(sms.getServiceName()) && StrUtil.isNotBlank(sms.getType())) {
            Setting codeSetting = settingService.get(sms.getServiceName() + "_" + sms.getType());
            codeSetting.setValue(sms.getTemplateCode());
            settingService.saveOrUpdate(codeSetting);
        }
        return ResultUtil.success();
    }

    @RequestMapping(value = "/email/set", method = RequestMethod.POST)
    @ApiOperation(value = "email配置")
    public Result emailSet(EmailSetting emailSetting) {

        Setting setting = settingService.get(SettingConstant.EMAIL_SETTING);
        if (StrUtil.isNotBlank(setting.getValue()) && !emailSetting.getChanged()) {
            String password = new Gson().fromJson(setting.getValue(), EmailSetting.class).getPassword();
            emailSetting.setPassword(password);
        }
        setting.setValue(new Gson().toJson(emailSetting));
        settingService.saveOrUpdate(setting);
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/other/set", method = RequestMethod.POST)
    @ApiOperation(value = "其他配置")
    public Result otherSet(OtherSetting otherSetting) {

        Setting setting = settingService.get(SettingConstant.OTHER_SETTING);
        setting.setValue(new Gson().toJson(otherSetting));
        settingService.saveOrUpdate(setting);
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/autoChat/set", method = RequestMethod.POST)
    @ApiOperation(value = "机器人配置")
    public Result autoChatSet(AutoChatSetting chatSetting) {

        Setting setting = settingService.get(SettingConstant.CHAT_SETTING);
        setting.setValue(new Gson().toJson(chatSetting));
        settingService.saveOrUpdate(setting);
        return ResultUtil.data(null);
    }

    @RequestMapping(value = "/notice/set", method = RequestMethod.POST)
    @ApiOperation(value = "公告配置")
    public Result noticeSet(NoticeSetting noticeSetting) {

        Setting setting = settingService.get(SettingConstant.NOTICE_SETTING);
        setting.setValue(new Gson().toJson(noticeSetting));
        settingService.saveOrUpdate(setting);
        return ResultUtil.data(null);
    }
}
