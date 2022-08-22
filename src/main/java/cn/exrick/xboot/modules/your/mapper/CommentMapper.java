package cn.exrick.xboot.modules.your.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.exrick.xboot.modules.your.entity.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 帖子评论信息数据处理层
 * @author xqp
 */
@Repository
public interface CommentMapper extends BaseMapper<Comment> {

}