package cn.exrick.xboot.modules.your.mapper;

import cn.exrick.xboot.modules.your.entity.Comment;
import cn.exrick.xboot.modules.your.entity.Post;
import cn.exrick.xboot.modules.your.entity.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.exrick.xboot.modules.your.entity.Answer;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论回复信息数据处理层
 * @author xqp
 */
@Repository
public interface AnswerMapper extends BaseMapper<Answer> {
        List<Answer> getInfo(long id,int a);

    List<Post> getPost(String postId);

    List<UserInfo> getUserInfo(String userId);

    List<Comment> getCommentInfo(String commentId);

    List<Answer> getAnswerInfo(String answerId);
}