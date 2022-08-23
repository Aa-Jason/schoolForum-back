package cn.exrick.xboot.modules.your.service;

import cn.exrick.xboot.modules.your.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.exrick.xboot.modules.your.entity.Support;

import java.util.List;

/**
 * 点赞信息接口
 * @author xqp
 */
public interface ISupportService extends IService<Support> {
    public List<Support> selectAllSupportById(String id);
}