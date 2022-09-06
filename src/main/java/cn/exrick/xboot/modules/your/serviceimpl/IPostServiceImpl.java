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

import java.util.ArrayList;
import java.util.Date;
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
    public List<Post> selectAllPostById(int id,int dif) {
        List<Post> posts = postMapper.selectAllPostById(id,dif);
        return posts;
    }

    @Override
    public List<PostVO> selectPostInnerId(int num,int part) {
        List<PostVO> postVOS = postMapper.selectPostInnerId(num,part);
        return postVOS;
    }

    @Override
    public List<PostVO> selectPostByKey(String key) {
        List<PostVO> postVOS = postMapper.selectPostByKey(key);
        return postVOS;
    }

    @Override
    public List<Post> posted(String postContent,String postPart,String postPicture,String postTitle,int userId,String nickName) {
        Post post=new Post();
        List<Post> list=new ArrayList<>();
        post.setCreateBy("jack");
        post.setUpdateBy(post.getCreateBy());
        post.setCreateTime(new Date());
        post.setUpdateTime(new Date());
        post.setPostPicture(postPicture);
        if(postContent!=null&&postPart!=null&&postTitle!=null){
            post.setPostContent(postContent);
            post.setPostPart(postPart);
            post.setPostTitle(postTitle);
            int i = postMapper.insertPosted(post.getCreateBy(),post.getCreateTime(),post.getDelFlag(),post.getUpdateBy(),
                    post.getUpdateTime(),post.getPostContent(),post.getPostPart(),post.getPostPicture(),
                    post.getPostTitle(),userId,nickName);
            if(i>0){
                list.add(post);
            }
        }

        return list;
    }

    @Override
    public void delPostById(int id) {

    }


}