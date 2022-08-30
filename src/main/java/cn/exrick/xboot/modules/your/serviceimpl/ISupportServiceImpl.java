package cn.exrick.xboot.modules.your.serviceimpl;

import cn.exrick.xboot.modules.your.entity.UserInfo;
import cn.exrick.xboot.modules.your.mapper.SupportMapper;
import cn.exrick.xboot.modules.your.entity.Support;
import cn.exrick.xboot.modules.your.service.ISupportService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 点赞信息接口实现
 * @author xqp
 */
@Slf4j
@Service
@Transactional
public class ISupportServiceImpl extends ServiceImpl<SupportMapper, Support> implements ISupportService {

    @Autowired
    private SupportMapper supportMapper;

    @Override
    public List<Support> getInfo(long id,int a) {

        List<Support> info = supportMapper.getInfo(id,a);
        return info;
    }

    @Override
    public List<Support> getPost(String postId) {
        List<Support> info = supportMapper.getPost(postId);
        return info;
    }

    @Override
    public List<Support> getUserInfo(String userId) {
        List<Support> info = supportMapper.getUserInfo(userId);
        return info;
    }

    @Override
    public List<Support> getCommentInfo(String commentId) {
        List<Support> info = supportMapper.getCommentInfo(commentId);
        return info;
    }
}