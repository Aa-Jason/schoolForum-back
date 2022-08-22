package cn.exrick.xboot.config.security;

import cn.exrick.xboot.modules.app.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;

/**
 * @author Exrickx
 */
@Data
@AllArgsConstructor
public class TokenMember implements UserDetails, Serializable {

    private String id;

    private String username;

    private String nickname;

    private String mobile;

    private String email;

    private Integer type;

    private String permissions;

    private Integer platform;

    public TokenMember(Member member, Integer platform){

        this.id = member.getId();
        this.username = member.getUsername();
        this.nickname = member.getNickname();
        this.mobile = member.getMobile();
        this.email = member.getEmail();
        this.type = member.getType();
        this.permissions = member.getPermissions();

        this.platform = platform;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
