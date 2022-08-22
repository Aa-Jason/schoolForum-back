package cn.exrick.xboot.modules.your.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    /**
     * 主键id 用户id
     */

    private Integer userId;

    /**
     * 用户名 用户名
     */
    private String username;

    /**
     * 密码 密码
     */
    private String password;




}