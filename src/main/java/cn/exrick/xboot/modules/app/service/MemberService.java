package cn.exrick.xboot.modules.app.service;

import cn.exrick.xboot.base.XbootBaseService;
import cn.exrick.xboot.common.vo.SearchVo;
import cn.exrick.xboot.modules.app.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 会员接口
 * @author Exrick
 */
public interface MemberService extends XbootBaseService<Member, String> {

    /**
     * 多条件分页获取
     * @param member
     * @param searchVo
     * @param pageable
     * @return
     */
    Page<Member> findByCondition(Member member, SearchVo searchVo, Pageable pageable);

    /**
     * 通过用户名获取用户
     * @param username
     * @return
     */
    Member findByUsername(String username);

    /**
     * 通过手机获取用户
     * @param mobile
     * @return
     */
    Member findByMobile(String mobile);
}