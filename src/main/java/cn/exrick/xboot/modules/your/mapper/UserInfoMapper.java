package cn.exrick.xboot.modules.your.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.exrick.xboot.modules.your.entity.UserInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户信息表数据处理层
 * @author xqp
 */
@Repository
public interface UserInfoMapper extends BaseMapper<UserInfo> {

}