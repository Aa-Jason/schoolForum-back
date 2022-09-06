package cn.exrick.xboot.modules.your.service;

import cn.exrick.xboot.modules.your.entity.Answer;
import cn.exrick.xboot.modules.your.entity.Post;
import cn.exrick.xboot.modules.your.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.exrick.xboot.modules.your.entity.Comment;

import java.util.Date;
import java.util.List;

/**
 * 帖子评论信息接口
 * @author xqp
 */
public interface ICommentService extends IService<Comment> {

    List<Comment> getInfo(long id);

    List<Post> getPost(String postId);

    List<UserInfo> getUserInfo(String userId);

    List<Comment> getCommentByPostId(long postId);

    List<Answer> getAnswerByCommentId(String commentId);

    void newComment(int postId, String commentContent, String nickName, int commentUserId, int postUserId, Date time);
}