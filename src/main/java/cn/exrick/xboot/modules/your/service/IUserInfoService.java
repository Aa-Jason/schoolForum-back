package cn.exrick.xboot.modules.your.service;

import cn.exrick.xboot.modules.your.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 用户信息表接口
 * @author xqp
 */
public interface IUserInfoService extends IService<UserInfo> {

    List<UserInfo> loginAuth(String name, String pwd);

    List<UserInfo> getUserIdByUsername(String name);

    List<UserInfo> getAll();

}