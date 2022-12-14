package cn.exrick.xboot.modules.your.service;

import cn.exrick.xboot.modules.your.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.exrick.xboot.modules.your.entity.Answer;

import java.util.List;

/**
 * 评论回复信息接口
 * @author xqp
 */
public interface IAnswerService extends IService<Answer> {
    public List<Answer> selectAllAnswerById(String id);
}