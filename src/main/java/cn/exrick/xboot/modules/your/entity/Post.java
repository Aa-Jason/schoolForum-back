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
@Table(name = "s_post")
@TableName("s_post")
@ApiModel(value = "帖子信息表")
public class Post extends XbootBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "帖子标题")
    @TableField("postTitle")
    private String postTitle;

    @ApiModelProperty(value = "帖子内容")
    @TableField("postContent")
    private String postContent;

    @ApiModelProperty(value = "帖子图片")
    @TableField("postPicture")
    private String postPicture;

    @ApiModelProperty(value = "帖子分区")
    @TableField("postPart")
    private String postPart;

    @ApiModelProperty(value = "发帖人ID")
    @TableField("userId")
    private String userId;

    @ApiModelProperty(value = "发帖人昵称")
    @TableField("nickName")
    private String nickName;

    @ApiModelProperty(value = "帖子点赞数")
    @TableField("supportCount")
    private String supportCount;

    @ApiModelProperty(value = "帖子评论数")
    @TableField("commentCount")
    private String commentCount;


}