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
@Table(name = "s_answer")
@TableName("s_answer")
@ApiModel(value = "评论回复信息")
public class Answer extends XbootBaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "发表回复人的Id")
    @TableField("answerUserId")
    private String answerUserId;

    @ApiModelProperty(value = "发表回复人的昵称")
    @TableField("nickName")
    private String nickName;

    @ApiModelProperty(value = "评论的Id")
    @TableField("commentId")
    private String commentId;

    @ApiModelProperty(value = "回复内容")
    @TableField("answerContent")
    private String answerContent;

    @ApiModelProperty(value = "目标用户id")
    @TableField("targetUserId")
    private String targetUserId;

    @ApiModelProperty(value = "目标回复id")
    @TableField("targetAnswerId")
    private String targetAnswerId;

    @ApiModelProperty(value = "所在帖子Id")
    @TableField("targetPostId")
    private String targetPostId;

}