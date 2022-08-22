package cn.exrick.xboot.modules.your.serviceimpl;

import cn.exrick.xboot.modules.your.entity.UserInfo;
import cn.exrick.xboot.modules.your.mapper.UserInfoMapper;
import cn.exrick.xboot.modules.your.service.IUserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户信息表接口实现
 * @author xqp
 */
@Slf4j
@Service
@Transactional
public class IUserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    /**
     * 验证账号和密码
     * @param name
     * @param pwd
     * @return
     */
    @Override
    public List<UserInfo> loginAuth(String name,String pwd) {
        List<UserInfo> login = userInfoMapper.loginAuth(name,pwd);
        return login;
    }

    /**
     * 根据账号获取用户id
     * @param name
     * @return
     */
    @Override
    public List<UserInfo> getUserIdByUsername(String name) {
        List<UserInfo> userid = userInfoMapper.getUserIdByUsername(name);
        return userid;
    }

    @Override
    public List<UserInfo> getAll() {
        List<UserInfo> all = userInfoMapper.getAll();
        return all;
    }
}