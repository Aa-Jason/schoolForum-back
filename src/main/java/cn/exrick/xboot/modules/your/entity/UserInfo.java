package cn.exrick.xboot.modules.your.entity;

import cn.exrick.xboot.base.XbootBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author xqp
 */
@Data
@Accessors(chain = true)
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "s_user_info")
@TableName("s_user_info")
@ApiModel(value = "用户信息表")
public class UserInfo extends XbootBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "账号")
    @TableField("username")
    private String username;

    @ApiModelProperty(value = "密码")
    @TableField("password")
    private String password;

    @ApiModelProperty(value = "用户昵称")
    @TableField("nickName")
    private String nickName;

    @ApiModelProperty(value = "头像")
    @TableField("avatar")
    private String avatar;

    @ApiModelProperty(value = "用户类型")
    @TableField("userType")
    private String userType;

    @ApiModelProperty(value = "学号")
    @TableField("studentId")
    private Integer studentId;

    @ApiModelProperty(value = "学校")
    @TableField("school")
    private String school;

    @ApiModelProperty(value = "联系方式")
    @TableField("contactDetail")
    private String contactDetail;

}