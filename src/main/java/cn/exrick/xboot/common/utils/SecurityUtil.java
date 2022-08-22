package cn.exrick.xboot.common.utils;

import cn.exrick.xboot.common.constant.CommonConstant;
import cn.exrick.xboot.common.constant.SecurityConstant;
import cn.exrick.xboot.common.constant.UserConstant;
import cn.exrick.xboot.common.exception.XbootException;
import cn.exrick.xboot.common.redis.RedisTemplateHelper;
import cn.exrick.xboot.config.properties.XbootAppTokenProperties;
import cn.exrick.xboot.config.properties.XbootTokenProperties;
import cn.exrick.xboot.config.security.TokenMember;
import cn.exrick.xboot.config.security.TokenUser;
import cn.exrick.xboot.modules.app.dao.MemberDao;
import cn.exrick.xboot.modules.app.entity.Member;
import cn.exrick.xboot.modules.base.dao.DepartmentDao;
import cn.exrick.xboot.modules.base.dao.UserDao;
import cn.exrick.xboot.modules.base.dao.mapper.PermissionMapper;
import cn.exrick.xboot.modules.base.dao.mapper.UserRoleMapper;
import cn.exrick.xboot.modules.base.entity.Department;
import cn.exrick.xboot.modules.base.entity.Permission;
import cn.exrick.xboot.modules.base.entity.Role;
import cn.exrick.xboot.modules.base.entity.User;
import cn.exrick.xboot.modules.base.service.mybatis.IUserRoleService;
import cn.exrick.xboot.modules.base.vo.PermissionDTO;
import cn.exrick.xboot.modules.base.vo.RoleDTO;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Exrickx
 */
@Component
public class SecurityUtil {

    @Autowired
    private XbootTokenProperties tokenProperties;

    @Autowired
    private XbootAppTokenProperties appTokenProperties;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private IUserRoleService iUserRoleService;

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private RedisTemplateHelper redisTemplate;

    /**
     * -------------------ToB-------------------------
     */

    public User findUserByUsername(String username) {

        String key = "username::" + username;
        // 读取缓存
        String res = redisTemplate.get(key);
        if (StrUtil.isNotBlank(res)) {
            return userToDTO(new Gson().fromJson(res, User.class));
        }
        User user = userToDTO(userDao.findByUsername(username));
        // 缓存
        redisTemplate.set(key, new Gson().toJson(user), 15L, TimeUnit.DAYS);
        return user;
    }

    public User findUserByMobile(String mobile) {

        return userToDTO(userDao.findByMobile(mobile));
    }

    public User findUserByEmail(String email) {

        return userToDTO(userDao.findByEmail(email));
    }

    public User userToDTO(User user) {

        if (user == null) {
            return null;
        }
        // 关联角色
        List<Role> roleList = userRoleMapper.findByUserId(user.getId());
        List<RoleDTO> roleDTOList = roleList.stream().map(e ->
                new RoleDTO().setId(e.getId()).setName(e.getName())).collect(Collectors.toList());
        user.setRoles(roleDTOList);
        // 关联权限菜单
        List<Permission> permissionList = permissionMapper.findByUserId(user.getId());
        List<PermissionDTO> permissionDTOList = permissionList.stream()
                .filter(e -> CommonConstant.PERMISSION_OPERATION.equals(e.getType()))
                .map(e -> new PermissionDTO().setTitle(e.getTitle()).setPath(e.getPath())).collect(Collectors.toList());
        user.setPermissions(permissionDTOList);
        return user;
    }

    public User checkUserPassword(String username, String password) {

        User user;
        // 校验用户名
        if (NameUtil.mobile(username)) {
            user = findUserByEmail(username);
        } else if (NameUtil.email(username)) {
            user = findUserByEmail(username);
        } else {
            user = findUserByUsername(username);
        }
        if (user == null) {
            return null;
        }
        // 校验密码
        Boolean isValid = new BCryptPasswordEncoder().matches(password, user.getPassword());
        if (!isValid) {
            return null;
        }
        return user;
    }

    public String getToken(String username, Boolean saveLogin) {

        if (StrUtil.isBlank(username)) {
            throw new XbootException("username不能为空");
        }
        User user = findUserByUsername(username);
        return getToken(user, saveLogin);
    }

    public String getToken(User user, Boolean saveLogin) {

        if (user == null) {
            throw new XbootException("user不能为空");
        }
        if (UserConstant.USER_STATUS_LOCK.equals(user.getStatus())) {
            throw new XbootException("账户被禁用，请联系管理员");
        }
        Boolean saved = false;
        if (saveLogin == null || saveLogin) {
            saved = true;
            if (!tokenProperties.getRedis()) {
                tokenProperties.setTokenExpireTime(tokenProperties.getSaveLoginTime() * 60 * 24);
            }
        }
        // 生成token
        String token;
        TokenUser tokenUser;
        if (tokenProperties.getRedis()) {
            // redis
            token = IdUtil.simpleUUID();
            tokenUser = new TokenUser(user, tokenProperties.getStorePerms(), saved);
            // 单设备登录 之前的token失效
            if (tokenProperties.getSdl()) {
                String oldToken = redisTemplate.get(SecurityConstant.USER_TOKEN + user.getUsername());
                if (StrUtil.isNotBlank(oldToken)) {
                    redisTemplate.delete(SecurityConstant.TOKEN_PRE + oldToken);
                }
            }
            // 是否记住账号/保存登录
            if (saved) {
                redisTemplate.set(SecurityConstant.USER_TOKEN + user.getUsername(), token, tokenProperties.getSaveLoginTime(), TimeUnit.DAYS);
                redisTemplate.set(SecurityConstant.TOKEN_PRE + token, new Gson().toJson(tokenUser), tokenProperties.getSaveLoginTime(), TimeUnit.DAYS);
            } else {
                redisTemplate.set(SecurityConstant.USER_TOKEN + user.getUsername(), token, tokenProperties.getTokenExpireTime(), TimeUnit.MINUTES);
                redisTemplate.set(SecurityConstant.TOKEN_PRE + token, new Gson().toJson(tokenUser), tokenProperties.getTokenExpireTime(), TimeUnit.MINUTES);
            }
        } else {
            // JWT 不缓存权限 避免JWT长度过长
            tokenUser = new TokenUser(user, false, null);
            token = SecurityConstant.TOKEN_SPLIT + Jwts.builder()
                    // 主题 放入用户信息
                    .setSubject(new Gson().toJson(tokenUser))
                    // 失效时间
                    .setExpiration(new Date(System.currentTimeMillis() + tokenProperties.getTokenExpireTime() * 60 * 1000))
                    // 签名算法和密钥
                    .signWith(SignatureAlgorithm.HS512, SecurityConstant.JWT_SIGN_KEY)
                    .compact();
        }
        // 记录日志使用
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(tokenUser, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return token;
    }

    /**
     * 获取当前登录用户 包含所有信息
     * @return
     */
    public User getCurrUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new XbootException("未检测到登录用户");
        }
        return findUserByUsername(authentication.getName());
    }

    /**
     * 获取当前登录用户部分基本信息 id、username、nickname、mobile、email、departmentId、type、permissions（角色和菜单名）
     * @return
     */
    public User getCurrUserSimple() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new XbootException("未检测到登录用户");
        }
        TokenUser tokenUser = (TokenUser) authentication.getPrincipal();
        User user = new User().setUsername(tokenUser.getUsername()).setNickname(tokenUser.getNickname()).
                setMobile(tokenUser.getMobile()).setEmail(tokenUser.getEmail()).setDepartmentId(tokenUser.getDepartmentId()).setType(tokenUser.getType());
        if (tokenUser.getPermissions() != null && !tokenUser.getPermissions().isEmpty()) {
            user.setPermissions(tokenUser.getPermissions().stream().map(e -> new PermissionDTO().setTitle(e)).collect(Collectors.toList()));
        }
        user.setId(tokenUser.getId());
        return user;
    }

    /**
     * 获取当前用户数据权限 是否为仅自己的权限
     */
    public Boolean isSelfDataPerm() {

        List<String> list = getDeparmentIds();
        if (list == null) {
            return false;
        }
        return list.contains("-2");
    }

    /**
     * 获取当前用户数据权限 null代表具有所有权限 包含值为-1的数据代表无任何权限 包含值为-2的数据代表仅自己的权限
     */
    public List<String> getDeparmentIds() {

        List<String> deparmentIds = new ArrayList<>();
        User u = getCurrUserSimple();
        // 读取缓存
        String key = "userRole::depIds:" + u.getId();
        String v = redisTemplate.get(key);
        if (StrUtil.isNotBlank(v)) {
            deparmentIds = new Gson().fromJson(v, new TypeToken<List<String>>() {
            }.getType());
            return deparmentIds;
        }
        // 当前用户拥有角色
        List<Role> roles = iUserRoleService.findByUserId(u.getId());
        // 判断有无全部数据的角色
        Boolean flagAll = false;
        for (Role r : roles) {
            if (r.getDataType() == null || r.getDataType().equals(CommonConstant.DATA_TYPE_ALL)) {
                flagAll = true;
                break;
            }
        }
        // 包含全部权限返回null
        if (flagAll) {
            return null;
        }
        // 每个角色判断 求并集
        for (Role r : roles) {
            if (r.getDataType().equals(CommonConstant.DATA_TYPE_UNDER)) {
                // 本部门及以下
                if (StrUtil.isBlank(u.getDepartmentId())) {
                    // 用户无部门
                    deparmentIds.add("-1");
                } else {
                    // 递归获取自己与子级
                    List<String> ids = new ArrayList<>();
                    getDepRecursion(u.getDepartmentId(), ids);
                    deparmentIds.addAll(ids);
                }
            } else if (r.getDataType().equals(CommonConstant.DATA_TYPE_SAME)) {
                // 本部门
                if (StrUtil.isBlank(u.getDepartmentId())) {
                    // 用户无部门
                    deparmentIds.add("-1");
                } else {
                    deparmentIds.add(u.getDepartmentId());
                }
            } else if (r.getDataType().equals(CommonConstant.DATA_TYPE_CUSTOM)) {
                // 自定义
                List<String> depIds = iUserRoleService.findDepIdsByUserId(u.getId());
                if (depIds == null || depIds.size() == 0) {
                    deparmentIds.add("-1");
                } else {
                    deparmentIds.addAll(depIds);
                }
            } else if (r.getDataType().equals(CommonConstant.DATA_TYPE_SELF)) {
                // 自己
                deparmentIds.add("-2");
            }
        }
        // 去重
        LinkedHashSet<String> set = new LinkedHashSet<>(deparmentIds.size());
        set.addAll(deparmentIds);
        deparmentIds.clear();
        deparmentIds.addAll(set);
        // 缓存
        redisTemplate.set(key, new Gson().toJson(deparmentIds), 15L, TimeUnit.DAYS);
        return deparmentIds;
    }

    private void getDepRecursion(String departmentId, List<String> ids) {

        Department department = departmentDao.getById(departmentId);
        ids.add(department.getId());
        if (department.getIsParent() != null && department.getIsParent()) {
            // 获取其下级
            List<Department> departments =
                    departmentDao.findByParentIdAndStatusOrderBySortOrder(departmentId, CommonConstant.STATUS_NORMAL);
            departments.forEach(d -> {
                getDepRecursion(d.getId(), ids);
            });
        }
    }

    /**
     * 通过用户名获取用户拥有权限
     * @param username
     */
    public List<GrantedAuthority> getCurrUserPerms(String username) {

        List<GrantedAuthority> authorities = new ArrayList<>();
        User user = findUserByUsername(username);
        if (user == null || user.getPermissions() == null || user.getPermissions().isEmpty()) {
            return authorities;
        }
        for (PermissionDTO p : user.getPermissions()) {
            authorities.add(new SimpleGrantedAuthority(p.getTitle()));
        }
        return authorities;
    }

    /**
     * -------------------App ToC-------------------------
     */

    public Member findMemberByUsername(String username) {

        return memberDao.findByUsername(username);
    }

    public Member findMemberByMobile(String mobile) {

        String key = "member::" + mobile;
        // 读取缓存
        String res = redisTemplate.get(key);
        if (StrUtil.isNotBlank(res)) {
            return new Gson().fromJson(res, Member.class);
        }
        Member member = memberDao.findByMobile(mobile);
        // 缓存
        redisTemplate.set(key, new Gson().toJson(member), 15L, TimeUnit.DAYS);
        return member;
    }

    public String getAppToken(String username, Integer platform) {

        if (StrUtil.isBlank(username)) {
            throw new XbootException("username不能为空");
        }
        Member member = findMemberByUsername(username);
        return getAppToken(member, platform);
    }

    public String getAppToken(Member member, Integer platform) {

        if (member == null) {
            throw new XbootException("member不能为空");
        }
        if (UserConstant.USER_STATUS_LOCK.equals(member.getStatus())) {
            throw new XbootException("账户被禁用，请联系管理员");
        }
        // 生成token
        String token;
        TokenMember tokenMember;
        if (appTokenProperties.getRedis()) {
            // redis
            token = IdUtil.simpleUUID();
            tokenMember = new TokenMember(member, platform);
            String key = SecurityConstant.MEMBER_TOKEN + tokenMember.getUsername() + ":" + platform;
            // 单平台登录 之前的token失效
            if (appTokenProperties.getSpl()) {
                String oldToken = redisTemplate.get(key);
                if (StrUtil.isNotBlank(oldToken)) {
                    redisTemplate.delete(SecurityConstant.TOKEN_MEMBER_PRE + oldToken);
                }
            }
            redisTemplate.set(key, token, appTokenProperties.getTokenExpireTime(), TimeUnit.DAYS);
            redisTemplate.set(SecurityConstant.TOKEN_MEMBER_PRE + token, new Gson().toJson(tokenMember), appTokenProperties.getTokenExpireTime(), TimeUnit.DAYS);
        } else {
            // JWT
            tokenMember = new TokenMember(member, platform);
            token = SecurityConstant.TOKEN_SPLIT + Jwts.builder()
                    // 主题 放入会员信息
                    .setSubject(new Gson().toJson(tokenMember))
                    // 失效时间
                    .setExpiration(new Date(System.currentTimeMillis() + appTokenProperties.getTokenExpireTime() * 60 * 1000))
                    // 签名算法和密钥
                    .signWith(SignatureAlgorithm.HS512, SecurityConstant.JWT_SIGN_KEY)
                    .compact();
        }
        // 记录日志使用
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(tokenMember, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return token;
    }

    /**
     * 获取当前登录会员信息 包含所有
     * @return
     */
    public Member getCurrMember() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new XbootException("未检测到登录会员");
        }
        return findMemberByUsername(authentication.getName());
    }

    /**
     * 获取当前登录会员部分信息 id、username、nickname、mobile、email、type、permissions、platform
     * @return
     */
    public Member getCurrMemberSimple() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new XbootException("未检测到登录会员");
        }
        TokenMember tokenMember = (TokenMember) authentication.getPrincipal();
        Member member = new Member().setUsername(tokenMember.getUsername()).setNickname(tokenMember.getNickname())
                .setMobile(tokenMember.getMobile()).setEmail(tokenMember.getEmail()).setType(tokenMember.getType())
                .setPermissions(tokenMember.getPermissions()).setPlatform(tokenMember.getPlatform());
        member.setId(tokenMember.getId());
        return member;
    }
}
