package cn.exrick.xboot.modules.your.serviceimpl;

import cn.exrick.xboot.modules.your.mapper.AnswerMapper;
import cn.exrick.xboot.modules.your.entity.Answer;
import cn.exrick.xboot.modules.your.service.IAnswerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论回复信息接口实现
 * @author xqp
 */
@Slf4j
@Service
@Transactional
public class IAnswerServiceImpl extends ServiceImpl<AnswerMapper, Answer> implements IAnswerService {

    @Autowired
    private AnswerMapper answerMapper;

    @Override
    public List<Answer> selectAllAnswerById(String id) {
        List<Answer> answers = answerMapper.selectAllAnswerById(id);
        return answers;
    }
}