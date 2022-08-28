package cn.exrick.xboot.modules.your.serviceimpl;

import cn.exrick.xboot.common.utils.ResultUtil;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.modules.your.mapper.PostMapper;
import cn.exrick.xboot.modules.your.entity.Post;
import cn.exrick.xboot.modules.your.service.IPostService;
import cn.exrick.xboot.modules.your.vo.PostVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 帖子信息表接口实现
 * @author xqp
 */
@Slf4j
@Service
@Transactional
public class IPostServiceImpl extends ServiceImpl<PostMapper, Post> implements IPostService {

    @Autowired
    private PostMapper postMapper;

    @Override
    public List<Post> selectAllPost() {
        List<Post> posts = postMapper.selectAllPost();
        return posts;
    }

    @Override
    public List<Post> selectAllPostById(String id) {
        List<Post> posts = postMapper.selectAllPostById(id);
        return posts;
    }

    @Override
    public List<PostVO> selectPostInnerId(int num,int part) {
        List<PostVO> postVOS = postMapper.selectPostInnerId(num,part);
        return postVOS;
    }

//    public Result<Post> selectAllPost(){
//        List<Post> posts = postMapper.selectAllPost();
//
//    }
}