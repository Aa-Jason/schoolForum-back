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
 * @author Exrick
 */
@Data
@Accessors(chain = true)
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "s_tabel_test")
@TableName("s_tabel_test")
@ApiModel(value = "建表测试")
public class TabelTest extends XbootBaseEntity {

    private static final long serialVersionUID = 1L;
    // 发帖人id 昵称 头像从用户登录表取
    @ApiModelProperty(value = "帖子id")
    @TableField("postId")
    private int postId;

    @ApiModelProperty(value = "帖子标题")
    @TableField("postTitl")
    private String postTitl;

    @ApiModelProperty(value = "帖子内容")
    @TableField("postText")
    private String postText;

    @ApiModelProperty(value = "帖子图片")
    @TableField("postPicture")
    private String postPicture;

    @ApiModelProperty(value = "帖子分区")
    @TableField("postPart")
    private String postPart;

//发帖时间就是字段中的创建时间
//帖子点赞数  帖子评论数从点赞表取

}