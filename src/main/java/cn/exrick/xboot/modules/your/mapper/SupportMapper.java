package cn.exrick.xboot.modules.your.mapper;

import cn.exrick.xboot.modules.your.entity.Post;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.exrick.xboot.modules.your.entity.Support;

import java.util.List;

/**
 * 点赞信息数据处理层
 * @author xqp
 */
public interface SupportMapper extends BaseMapper<Support> {
    public List<Support> selectAllSupportById(String id);
}