package cn.exrick.xboot.config.security;

import cn.exrick.xboot.modules.base.entity.User;
import cn.exrick.xboot.modules.base.vo.PermissionDTO;
import cn.exrick.xboot.modules.base.vo.RoleDTO;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Exrickx
 */
@Data
@AllArgsConstructor
public class TokenUser implements UserDetails, Serializable {

    private String id;

    private String username;

    private String nickname;

    private String mobile;

    private String email;

    private String departmentId;

    private Integer type;

    private List<String> permissions;

    private Boolean saveLogin;

    public TokenUser(User user, Boolean storePerms, Boolean saveLogin){

        this.id = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.mobile = user.getMobile();
        this.email = user.getEmail();
        this.departmentId = user.getDepartmentId();
        this.type = user.getType();
        this.saveLogin = saveLogin;

        // 缓存权限
        List<String> list = new ArrayList<>();
        if (storePerms) {
            for (PermissionDTO p : user.getPermissions()) {
                if (StrUtil.isNotBlank(p.getTitle()) && StrUtil.isNotBlank(p.getPath())) {
                    list.add(p.getTitle());
                }
            }
            for (RoleDTO r : user.getRoles()) {
                list.add(r.getName());
            }
            this.setPermissions(list);
        }
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
