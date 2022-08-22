package cn.exrick.xboot.modules.base.dao.elasticsearch;

import cn.exrick.xboot.modules.base.entity.elasticsearch.EsLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * @author Exrickx
 */
public interface EsLogDao extends ElasticsearchRepository<EsLog, String> {

    /**
     * 通过类型获取
     * @param type
     * @param pageable
     * @return
     */
    Page<EsLog> findByLogType(Integer type, Pageable pageable);

    /**
     * 仅有关键词key搜索
     * @param name
     * @param requestUrl
     * @param requestType
     * @param requestParam
     * @param username
     * @param ip
     * @param ipInfo
     * @param device
     * @param type
     * @param pageable
     * @return
     */
    Page<EsLog> findByNameLikeOrRequestUrlLikeOrRequestTypeLikeOrRequestParamOrUsernameLikeOrIpLikeOrIpInfoLikeOrDeviceLikeAndLogTypeIs
            (String name, String requestUrl, String requestType, String requestParam, String username, String ip, String ipInfo, String device, Integer type, Pageable pageable);

    /**
     * 仅有时间范围搜索
     * @param start
     * @param end
     * @param pageable
     * @return
     */
    Page<EsLog> findByTimeMillisGreaterThanEqualAndTimeMillisLessThanEqualAndLogTypeIs(Long start, Long end, Integer type, Pageable pageable);

    /**
     * 关键词与时间范围搜索
     * @param name
     * @param requestUrl
     * @param requestType
     * @param requestParam
     * @param username
     * @param ip
     * @param ipInfo
     * @param device
     * @param start
     * @param end
     * @param type
     * @param pageable
     * @return
     */
    Page<EsLog> findByNameLikeOrRequestUrlLikeOrRequestTypeLikeOrRequestParamOrUsernameLikeOrIpLikeOrIpInfoLikeOrDeviceLikeAndTimeMillisGreaterThanEqualAndTimeMillisLessThanEqualAndLogTypeIs
            (String name, String requestUrl, String requestType, String requestParam, String username, String ip, String ipInfo, String device, Long start, Long end, Integer type, Pageable pageable);
}
