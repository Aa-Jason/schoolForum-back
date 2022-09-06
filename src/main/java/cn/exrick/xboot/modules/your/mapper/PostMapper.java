package cn.exrick.xboot.modules.your.mapper;

import cn.exrick.xboot.modules.your.vo.PostVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.exrick.xboot.modules.your.entity.Post;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 帖子信息表数据处理层
 * @author xqp
 */
@Repository
public interface PostMapper extends BaseMapper<Post> {
    public List<Post> selectAllPost();

    public List<Post> selectAllPostById(long id,int dif);

    List<PostVO> selectPostInnerId(int num, int part);

    List<PostVO> selectPostByKey(String key);

    int insertPosted(String create_by, Date create_time, int del_flag,
                     String update_by, Date update_time,
                     String post_content, String post_part, String post_picture,
                     String post_title, int user_id, String nick_name);
    public void delPostById(int id);
}