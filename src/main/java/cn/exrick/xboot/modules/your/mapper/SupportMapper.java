package cn.exrick.xboot.modules.your.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.exrick.xboot.modules.your.entity.Support;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 点赞信息数据处理层
 * @author xqp
 */
@Repository
public interface SupportMapper extends BaseMapper<Support> {

    List<Support> getInfo(long id);

    List<Support> getPost(String postId);

    List<Support> getUserInfo(String userId);

    List<Support> getCommentInfo(String commentId);
}