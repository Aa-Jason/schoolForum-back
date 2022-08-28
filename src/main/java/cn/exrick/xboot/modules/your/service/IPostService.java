package cn.exrick.xboot.modules.your.service;

import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.modules.your.vo.PostVO;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.exrick.xboot.modules.your.entity.Post;

import java.util.List;

/**
 * 帖子信息表接口
 * @author xqp
 */
public interface IPostService extends IService<Post> {
    public List<Post> selectAllPost();

    public List<Post> selectAllPostById(String id);

    public List<PostVO> selectPostInnerId(int num,int part);
}