package cn.exrick.xboot.modules.your.serviceimpl;

import cn.exrick.xboot.modules.your.dao.UsersMapper;
import cn.exrick.xboot.modules.your.entity.Users;
import cn.exrick.xboot.modules.your.service.UserServiceTest;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;


@Service
public class UserServiceTestImpl implements UserServiceTest {

    @Autowired
    private UsersMapper usersMapper;


    @Override
    public String checkLogin(String name, String pwd) {


        List<Users> users = usersMapper.checkLogin(name,pwd);

        if(users.size() == 0){
            return "no";
        }else{
           // String md5Pwd = MD5Utils.md5(pwd);
            if(pwd.equals(users.get(0).getPassword())){
                //如果登录验证成功，则需要生成令牌token（token就是按照特定规则生成的字符串）
                //使用jwt规则生成token字符串
                JwtBuilder builder = Jwts.builder();

                HashMap<String,Object> map = new HashMap<>();
                map.put("key1","value1");
                map.put("key2","value2");

                String token = builder.setSubject(name)                     //主题，就是token中携带的数据
                        .setIssuedAt(new Date())                            //设置token的生成时间
                        .setId(users.get(0).getUserId() + "")               //设置用户id为token  id
                        .setClaims(map)                                     //map中可以存放用户的角色权限信息
                        .setExpiration(new Date(System.currentTimeMillis() + 24*60*60*1000)) //设置token过期时间
                        .signWith(SignatureAlgorithm.HS256, "QIANfeng6666")     //设置加密方式和加密密码
                        .compact();

                return "ok";
            }else{
                return "no";
            }
        }
    }
}
