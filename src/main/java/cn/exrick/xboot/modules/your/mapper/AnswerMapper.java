package cn.exrick.xboot.modules.your.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.exrick.xboot.modules.your.entity.Answer;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论回复信息数据处理层
 * @author xqp
 */
@Repository
public interface AnswerMapper extends BaseMapper<Answer> {

}