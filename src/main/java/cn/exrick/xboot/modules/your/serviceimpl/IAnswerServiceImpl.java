package cn.exrick.xboot.modules.your.serviceimpl;

import cn.exrick.xboot.modules.your.controller.AnswerController;
import cn.exrick.xboot.modules.your.entity.*;
import cn.exrick.xboot.modules.your.mapper.AnswerMapper;
import cn.exrick.xboot.modules.your.service.IAnswerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论回复信息接口实现
 * @author xqp
 */
@Slf4j
@Service
@Transactional
public class IAnswerServiceImpl extends ServiceImpl<AnswerMapper, Answer> implements IAnswerService {

    @Autowired
    private AnswerMapper answerMapper;

    @Override
    public List<Answer> getInfo(long id,int a) {
        List<Answer> info = answerMapper.getInfo(id,a);
        return info;
    }

    @Override
    public List<Post> getPost(String postId) {
        List<Post> info = answerMapper.getPost(postId);
        return info;
    }

    @Override
    public List<UserInfo> getUserInfo(String userId) {
        List<UserInfo> info = answerMapper.getUserInfo(userId);
        return info;
    }

    @Override
    public List<Comment> getCommentInfo(String commentId) {
        List<Comment> info = answerMapper.getCommentInfo(commentId);
        return info;
    }

    @Override
    public List<Answer> getAnswerInfo(String answerId) {
        List<Answer> info = answerMapper.getAnswerInfo(answerId);
        return info;
    }

}