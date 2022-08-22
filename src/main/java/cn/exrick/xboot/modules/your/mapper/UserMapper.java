package cn.exrick.xboot.modules.your.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.exrick.xboot.modules.your.entity.Users;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户信息表数据处理层
 * @author xqp
 */
@Repository
public interface UserMapper extends BaseMapper<Users> {

}