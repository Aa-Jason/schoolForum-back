package cn.exrick.xboot.modules.your.serviceimpl;

import cn.exrick.xboot.modules.your.mapper.TabelTestMapper;
import cn.exrick.xboot.modules.your.entity.TabelTest;
import cn.exrick.xboot.modules.your.service.ITabelTestService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 建表测试接口实现
 * @author Exrick
 */
@Slf4j
@Service
@Transactional
public class ITabelTestServiceImpl extends ServiceImpl<TabelTestMapper, TabelTest> implements ITabelTestService {

    @Autowired
    private TabelTestMapper tabelTestMapper;
}