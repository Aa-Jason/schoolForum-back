package cn.exrick.xboot.modules.your.dao;




import cn.exrick.xboot.modules.your.entity.Users;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersMapper {
    List<Users> checkLogin(String username , String password);
}