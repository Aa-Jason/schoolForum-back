package cn.exrick.xboot.modules.your.service;

import cn.exrick.xboot.modules.your.entity.Comment;
import cn.exrick.xboot.modules.your.entity.Post;
import cn.exrick.xboot.modules.your.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.exrick.xboot.modules.your.entity.Answer;

import java.util.Date;
import java.util.List;

/**
 * 评论回复信息接口
 * @author xqp
 */

public interface IAnswerService extends IService<Answer> {

    List<Answer> getInfo(long id,int a);

    List<Post> getPost(String postId);

    List<UserInfo> getUserInfo(String userId);

    List<Comment> getCommentInfo(String commentId);

    List<Answer> getAnswerInfo(String answerId);

    void newAnswer(int postId, String answerContent, String nickName, int answerUserId, int targetAnswerId, int targetUserId, int commentId, Date time, String targetNickName);
}