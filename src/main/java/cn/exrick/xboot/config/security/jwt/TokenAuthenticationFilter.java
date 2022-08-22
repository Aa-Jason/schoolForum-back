package cn.exrick.xboot.config.security.jwt;

import cn.exrick.xboot.common.constant.SecurityConstant;
import cn.exrick.xboot.common.redis.RedisTemplateHelper;
import cn.exrick.xboot.common.utils.ResponseUtil;
import cn.exrick.xboot.common.utils.SecurityUtil;
import cn.exrick.xboot.config.properties.XbootAppTokenProperties;
import cn.exrick.xboot.config.properties.XbootTokenProperties;
import cn.exrick.xboot.config.security.TokenMember;
import cn.exrick.xboot.config.security.TokenUser;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Exrickx
 */
@Slf4j
public class TokenAuthenticationFilter extends BasicAuthenticationFilter {

    private XbootTokenProperties tokenProperties;

    private XbootAppTokenProperties appTokenProperties;

    private RedisTemplateHelper redisTemplate;

    private SecurityUtil securityUtil;

    public TokenAuthenticationFilter(AuthenticationManager authenticationManager,
                                     XbootTokenProperties tokenProperties,
                                     XbootAppTokenProperties appTokenProperties,
                                     RedisTemplateHelper redisTemplate, SecurityUtil securityUtil) {
        super(authenticationManager);
        this.tokenProperties = tokenProperties;
        this.appTokenProperties = appTokenProperties;
        this.redisTemplate = redisTemplate;
        this.securityUtil = securityUtil;
    }

    public TokenAuthenticationFilter(AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint) {
        super(authenticationManager, authenticationEntryPoint);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String header = request.getHeader(SecurityConstant.HEADER);
        if (StrUtil.isBlank(header)) {
            header = request.getParameter(SecurityConstant.HEADER);
        }
        String appHeader = request.getHeader(SecurityConstant.APP_HEADER);
        if (StrUtil.isBlank(appHeader)) {
            appHeader = request.getParameter(SecurityConstant.APP_HEADER);
        }
        Boolean notValid = (StrUtil.isBlank(header) || (!tokenProperties.getRedis() && !header.startsWith(SecurityConstant.TOKEN_SPLIT)))
                && StrUtil.isBlank(appHeader);
        if (notValid) {
            chain.doFilter(request, response);
            return;
        }
        try {
            UsernamePasswordAuthenticationToken authentication = null;
            if (StrUtil.isNotBlank(header)) {
                authentication = getAuthentication(header, response);
            } else {
                authentication = getAppAuthentication(appHeader, response);
            }
            if (authentication == null) {
                return;
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.warn(e.toString());
        }

        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String header, HttpServletResponse response) {

        TokenUser tokenUser = null;
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (tokenProperties.getRedis()) {
            // redis
            String v = redisTemplate.get(SecurityConstant.TOKEN_PRE + header);
            if (StrUtil.isBlank(v)) {
                ResponseUtil.out(response, ResponseUtil.resultMap(false, 401, "登录已失效，请重新登录"));
                return null;
            }
            tokenUser = new Gson().fromJson(v, TokenUser.class);
            if (tokenProperties.getStorePerms()) {
                // 缓存了权限
                for (String ga : tokenUser.getPermissions()) {
                    authorities.add(new SimpleGrantedAuthority(ga));
                }
            } else {
                // 未缓存 读取权限数据
                authorities = securityUtil.getCurrUserPerms(tokenUser.getUsername());
            }
            if (!tokenUser.getSaveLogin()) {
                // 若未保存登录状态重新设置失效时间
                redisTemplate.set(SecurityConstant.USER_TOKEN + tokenUser.getUsername(), header, tokenProperties.getTokenExpireTime(), TimeUnit.MINUTES);
                redisTemplate.set(SecurityConstant.TOKEN_PRE + header, v, tokenProperties.getTokenExpireTime(), TimeUnit.MINUTES);
            }
        } else {
            // JWT
            try {
                // 解析token
                Claims claims = Jwts.parser()
                        .setSigningKey(SecurityConstant.JWT_SIGN_KEY)
                        .parseClaimsJws(header.replace(SecurityConstant.TOKEN_SPLIT, ""))
                        .getBody();
                // 获取用户
                tokenUser = new Gson().fromJson(claims.getSubject(), TokenUser.class);
                // JWT不缓存权限 读取权限数据 避免JWT长度过长
                authorities = securityUtil.getCurrUserPerms(tokenUser.getUsername());
            } catch (ExpiredJwtException e) {
                ResponseUtil.out(response, ResponseUtil.resultMap(false, 401, "登录已失效，请重新登录"));
            } catch (Exception e) {
                log.error(e.toString());
                ResponseUtil.out(response, ResponseUtil.resultMap(false, 500, "解析token错误"));
            }
        }

        if (tokenUser != null && StrUtil.isNotBlank(tokenUser.getUsername())) {
            return new UsernamePasswordAuthenticationToken(tokenUser, null, authorities);
        }
        return null;
    }

    private UsernamePasswordAuthenticationToken getAppAuthentication(String appHeader, HttpServletResponse response) {

        TokenMember tokenMember = null;
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (appTokenProperties.getRedis()) {
            // redis
            String v = redisTemplate.get(SecurityConstant.TOKEN_MEMBER_PRE + appHeader);
            if (StrUtil.isBlank(v)) {
                ResponseUtil.out(response, ResponseUtil.resultMap(false, 401, "会员登录已失效，请重新登录"));
                return null;
            }
            tokenMember = new Gson().fromJson(v, TokenMember.class);
            // 权限
            if (StrUtil.isNotBlank(tokenMember.getPermissions())) {
                authorities = Arrays.stream(tokenMember.getPermissions().split(",")).map(e -> new SimpleGrantedAuthority(e))
                        .collect(Collectors.toList());
            }
            // 重新设置失效时间
            redisTemplate.set(SecurityConstant.MEMBER_TOKEN + tokenMember.getUsername() + ":" + tokenMember.getPlatform(), appHeader, appTokenProperties.getTokenExpireTime(), TimeUnit.DAYS);
            redisTemplate.set(SecurityConstant.TOKEN_MEMBER_PRE + appHeader, v, appTokenProperties.getTokenExpireTime(), TimeUnit.DAYS);
        } else {
            // JWT
            try {
                // 解析token
                Claims claims = Jwts.parser()
                        .setSigningKey(SecurityConstant.JWT_SIGN_KEY)
                        .parseClaimsJws(appHeader.replace(SecurityConstant.TOKEN_SPLIT, ""))
                        .getBody();
                // 获取用户
                tokenMember = new Gson().fromJson(claims.getSubject(), TokenMember.class);
                // 权限
                if (StrUtil.isNotBlank(tokenMember.getPermissions())) {
                    authorities = Arrays.stream(tokenMember.getPermissions().split(",")).map(e -> new SimpleGrantedAuthority(e))
                            .collect(Collectors.toList());
                }
            } catch (ExpiredJwtException e) {
                ResponseUtil.out(response, ResponseUtil.resultMap(false, 401, "登录已失效，请重新登录"));
            } catch (Exception e) {
                log.error(e.toString());
                ResponseUtil.out(response, ResponseUtil.resultMap(false, 500, "解析token错误"));
            }
        }
        if (tokenMember != null && StrUtil.isNotBlank(tokenMember.getUsername())) {
            return new UsernamePasswordAuthenticationToken(tokenMember, null, authorities);
        }
        return null;
    }
}

