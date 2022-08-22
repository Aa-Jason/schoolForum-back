package cn.exrick.xboot.modules.base.serviceimpl.elasticsearch;

import cn.exrick.xboot.common.vo.SearchVo;
import cn.exrick.xboot.modules.base.dao.elasticsearch.EsLogDao;
import cn.exrick.xboot.modules.base.entity.elasticsearch.EsLog;
import cn.exrick.xboot.modules.base.service.elasticsearch.EsLogService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Exrickx
 */
@Service
@Transactional
@Slf4j
public class EsLogServiceImpl implements EsLogService {

    /**
     * 可选
     */
    @Autowired(required = false)
    private EsLogDao logDao;

    @Override
    public EsLog saveLog(EsLog esLog) {

        return logDao.save(esLog);
    }

    @Override
    public void deleteLog(String id) {

        logDao.deleteById(id);
    }

    @Override
    public void deleteAll() {

        logDao.deleteAll();
    }

    @Override
    public Page<EsLog> findByCondition(Integer type, String key, SearchVo searchVo, Pageable pageable) {

        if (type == null && StrUtil.isBlank(key) && StrUtil.isBlank(searchVo.getStartDate())) {
            // 无过滤条件获取全部
            return logDao.findAll(pageable);
        } else if (type != null && StrUtil.isBlank(key) && StrUtil.isBlank(searchVo.getStartDate())) {
            // 仅有type
            return logDao.findByLogType(type, pageable);
        }

        // 在有type条件下
        if (StrUtil.isNotBlank(key) && StrUtil.isBlank(searchVo.getStartDate()) && StrUtil.isBlank(searchVo.getEndDate())) {
            // 仅有key
            return logDao.findByNameLikeOrRequestUrlLikeOrRequestTypeLikeOrRequestParamOrUsernameLikeOrIpLikeOrIpInfoLikeOrDeviceLikeAndLogTypeIs
                    (key, key, key, key, key, key, key, key, type, pageable);
        } else if (StrUtil.isBlank(key) && StrUtil.isNotBlank(searchVo.getStartDate()) && StrUtil.isNotBlank(searchVo.getEndDate())) {
            // 仅有时间范围
            Long start = DateUtil.parse(searchVo.getStartDate()).getTime();
            Long end = DateUtil.endOfDay(DateUtil.parse(searchVo.getEndDate())).getTime();
            return logDao.findByTimeMillisGreaterThanEqualAndTimeMillisLessThanEqualAndLogTypeIs(start, end, type, pageable);
        } else {
            // 两者都有
            Long start = DateUtil.parse(searchVo.getStartDate()).getTime();
            Long end = DateUtil.endOfDay(DateUtil.parse(searchVo.getEndDate())).getTime();
            return logDao.findByNameLikeOrRequestUrlLikeOrRequestTypeLikeOrRequestParamOrUsernameLikeOrIpLikeOrIpInfoLikeOrDeviceLikeAndTimeMillisGreaterThanEqualAndTimeMillisLessThanEqualAndLogTypeIs
                    (key, key, key, key, key, key, key, key, start, end, type, pageable);
        }
    }
}
