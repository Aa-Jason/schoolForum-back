package cn.exrick.xboot.modules.your.service;

import cn.exrick.xboot.modules.your.controller.AnswerController;
import cn.exrick.xboot.modules.your.entity.Comment;
import cn.exrick.xboot.modules.your.entity.Post;
import cn.exrick.xboot.modules.your.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.exrick.xboot.modules.your.entity.Answer;
import org.springframework.stereotype.Repository;

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
}