package cn.exrick.xboot.modules.your.service;

import cn.exrick.xboot.modules.your.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.exrick.xboot.modules.your.entity.Comment;

import java.util.List;

/**
 * 帖子评论信息接口
 * @author xqp
 */
public interface ICommentService extends IService<Comment> {
    public List<Comment> selectAllCommentById(String id);
}