package cn.exrick.xboot.modules.your.mapper;

import cn.exrick.xboot.modules.your.entity.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户信息表数据处理层
 * @author xqp
 */
@Repository
public interface UserInfoMapper extends BaseMapper<UserInfo> {

    List<UserInfo> loginAuth(String name, String pwd);

    List<UserInfo> getUserIdByUsername(String name);

    List<UserInfo> getAll();
}