package cn.exrick.xboot.modules.base.serviceimpl;

import cn.exrick.xboot.common.utils.SecurityUtil;
import cn.exrick.xboot.common.vo.SearchVo;
import cn.exrick.xboot.modules.base.dao.UserDao;
import cn.exrick.xboot.modules.base.entity.User;
import cn.exrick.xboot.modules.base.service.UserService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户接口实现
 * @author Exrickx
 */
@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private SecurityUtil securityUtil;

    @Override
    public UserDao getRepository() {
        return userDao;
    }

    @Override
    public User findByUsername(String username) {

        return securityUtil.findUserByUsername(username);
    }

    @Override
    public User findByMobile(String mobile) {

        return securityUtil.findUserByMobile(mobile);
    }

    @Override
    public User findByEmail(String email) {

        return securityUtil.findUserByEmail(email);
    }

    @Override
    public Page<User> findByCondition(User user, SearchVo searchVo, Pageable pageable) {

        return userDao.findAll(new Specification<User>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

                Path<String> idField = root.get("id");
                Path<String> usernameField = root.get("username");
                Path<String> nicknameField = root.get("nickname");
                Path<String> mobileField = root.get("mobile");
                Path<String> emailField = root.get("email");
                Path<String> departmentIdField = root.get("departmentId");
                Path<String> sexField = root.get("sex");
                Path<Integer> typeField = root.get("type");
                Path<Integer> statusField = root.get("status");
                Path<Date> createTimeField = root.get("createTime");

                List<Predicate> list = new ArrayList<>();

                if (StrUtil.isNotBlank(user.getId())) {
                    list.add(cb.equal(idField, user.getId()));
                }

                // 模糊搜素
                if (StrUtil.isNotBlank(user.getUsername())) {
                    list.add(cb.like(usernameField, '%' + user.getUsername() + '%'));
                }
                if (StrUtil.isNotBlank(user.getNickname())) {
                    list.add(cb.like(nicknameField, '%' + user.getNickname() + '%'));
                }
                if (StrUtil.isNotBlank(user.getMobile())) {
                    list.add(cb.like(mobileField, '%' + user.getMobile() + '%'));
                }
                if (StrUtil.isNotBlank(user.getEmail())) {
                    list.add(cb.like(emailField, '%' + user.getEmail() + '%'));
                }

                // 部门
                if (StrUtil.isNotBlank(user.getDepartmentId())) {
                    list.add(cb.equal(departmentIdField, user.getDepartmentId()));
                }

                // 性别
                if (StrUtil.isNotBlank(user.getSex())) {
                    list.add(cb.equal(sexField, user.getSex()));
                }
                // 类型
                if (user.getType() != null) {
                    list.add(cb.equal(typeField, user.getType()));
                }
                // 状态
                if (user.getStatus() != null) {
                    list.add(cb.equal(statusField, user.getStatus()));
                }
                // 创建时间
                if (StrUtil.isNotBlank(searchVo.getStartDate()) && StrUtil.isNotBlank(searchVo.getEndDate())) {
                    Date start = DateUtil.parse(searchVo.getStartDate());
                    Date end = DateUtil.parse(searchVo.getEndDate());
                    list.add(cb.between(createTimeField, start, DateUtil.endOfDay(end)));
                }

                // 数据权限
                if (securityUtil.isSelfDataPerm()) {
                    // 仅自己
                    list.add(cb.equal(idField, securityUtil.getCurrUserSimple().getId()));
                } else {
                    List<String> depIds = securityUtil.getDeparmentIds();
                    if (depIds != null && depIds.size() > 0) {
                        list.add(departmentIdField.in(depIds));
                    }
                }

                Predicate[] arr = new Predicate[list.size()];
                cq.where(list.toArray(arr));
                return null;
            }
        }, pageable);
    }

    @Override
    public List<User> findByDepartmentId(String departmentId) {

        return userDao.findByDepartmentId(departmentId);
    }

    @Override
    public List<User> findByUsernameLikeAndStatus(String username, Integer status) {

        return userDao.findByUsernameLikeAndStatus(username, status);
    }

    @Override
    public void updateDepartmentTitle(String departmentId, String departmentTitle) {

        userDao.updateDepartmentTitle(departmentId, departmentTitle);
    }
}
