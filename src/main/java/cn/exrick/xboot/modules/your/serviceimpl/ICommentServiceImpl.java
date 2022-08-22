package cn.exrick.xboot.modules.your.serviceimpl;

import cn.exrick.xboot.modules.your.mapper.CommentMapper;
import cn.exrick.xboot.modules.your.entity.Comment;
import cn.exrick.xboot.modules.your.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
}