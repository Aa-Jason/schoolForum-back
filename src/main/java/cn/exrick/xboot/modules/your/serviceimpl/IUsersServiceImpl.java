package cn.exrick.xboot.modules.your.serviceimpl;

import cn.exrick.xboot.modules.your.mapper.UserMapper;
import cn.exrick.xboot.modules.your.entity.Users;
import cn.exrick.xboot.modules.your.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户信息表接口实现
 * @author xqp
 */
@Slf4j
@Service
@Transactional
public class IUsersServiceImpl extends ServiceImpl<UserMapper, Users> implements IUsersService {

    @Autowired
    private UserMapper userMapper;
}