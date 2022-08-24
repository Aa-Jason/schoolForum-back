package cn.exrick.xboot.modules.your.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.exrick.xboot.modules.your.entity.Support;

import java.util.List;

/**
 * 点赞信息接口
 * @author xqp
 */
public interface ISupportService extends IService<Support> {

    List<Support> getInfo(long id);

    List<Support> getPost(String postId);

    List<Support> getUserInfo(String userId);

    List<Support> getCommentInfo(String commentId);
}