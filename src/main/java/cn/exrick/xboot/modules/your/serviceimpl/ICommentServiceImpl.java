package cn.exrick.xboot.modules.your.serviceimpl;

import cn.exrick.xboot.modules.your.entity.Answer;
import cn.exrick.xboot.modules.your.entity.Post;
import cn.exrick.xboot.modules.your.entity.UserInfo;
import cn.exrick.xboot.modules.your.mapper.CommentMapper;
import cn.exrick.xboot.modules.your.entity.Comment;
import cn.exrick.xboot.modules.your.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 帖子评论信息接口实现
 * @author xqp
 */
@Slf4j
@Service
@Transactional
public class ICommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    @Autowired
    private CommentMapper commentMapper;


    @Override
    public List<Comment> getInfo(long id) {
        List<Comment> info = commentMapper.getInfo(id);
        return info;
    }

    @Override
    public List<Post> getPost(String postId) {
        List<Post> info = commentMapper.getPost(postId);
        return info;
    }

    @Override
    public List<UserInfo> getUserInfo(String userId) {
        List<UserInfo> info = commentMapper.getUserInfo(userId);
        return info;
    }

    @Override
    public List<Comment> getCommentByPostId(long postId) {
        List<Comment> info = commentMapper.getCommentByPostId(postId);
        return info;
    }

    @Override
    public List<Answer> getAnswerByCommentId(String commentId) {
        List<Answer> info = commentMapper.getAnswerByCommentId(commentId);
        return info;
    }

    @Override
    public void newComment(int postId, String commentContent, String nickName, int commentUserId, int postUserId, Date time) {

    }
}