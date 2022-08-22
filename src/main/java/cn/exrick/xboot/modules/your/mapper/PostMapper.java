package cn.exrick.xboot.modules.your.mapper;

import cn.exrick.xboot.modules.your.vo.PostVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.exrick.xboot.modules.your.entity.Post;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 帖子信息表数据处理层
 * @author xqp
 */
@Repository
public interface PostMapper extends BaseMapper<Post> {
    public List<Post> selectAllPost();

    public List<Post> selectAllPostById(String id);


    public List<PostVO> selectPostInnerId();
}