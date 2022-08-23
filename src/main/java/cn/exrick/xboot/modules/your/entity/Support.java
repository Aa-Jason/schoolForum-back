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
@Table(name = "s_support")
@TableName("s_support")
@ApiModel(value = "点赞信息")
public class Support extends XbootBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "点赞人的用户昵称")
    @TableField("supportNickName")
    private String supportNickName;

    @ApiModelProperty(value = "点赞人的用户id")
    @TableField("supportUserId")
    private String supportUserId;

    @ApiModelProperty(value = "被点赞的评论id")
    @TableField("targetCommentId")
    private String targetCommentId;

    @ApiModelProperty(value = "被点赞的帖子id")
    @TableField("targetPostId")
    private String targetPostId;

    @ApiModelProperty(value = "被点赞评论用户id")
    @TableField("targetCommentUserId")
    private String targetCommentUserId;

    @ApiModelProperty(value = "帖子所有者id")
    @TableField("targetPostUserId")
    private String targetPostUserId;



}