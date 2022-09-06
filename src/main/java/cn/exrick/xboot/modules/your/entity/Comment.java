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
@Table(name = "s_comment")
@TableName("s_comment")
@ApiModel(value = "帖子评论信息")
public class Comment extends XbootBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "帖子id")
    @TableField("postId")
    private int postId;

    @ApiModelProperty(value = "帖子所有者id")
    @TableField("postUserId")
    private int postUserId;

    @ApiModelProperty(value = "评论人id")
    @TableField("commentUserId")
    private int commentUserId;

    @ApiModelProperty(value = "评论人昵称")
    @TableField("nickName")
    private String nickName;

    @ApiModelProperty(value = "评论内容")
    @TableField("commentContent")
    private String commentContent;

    @ApiModelProperty(value = "评论点赞数")
    @TableField("commentSupportCount")
    private int commentSupportCount;

    @ApiModelProperty(value = "评论回复数")
    @TableField("commentAnswerCount")
    private int commentAnswerCount;
}