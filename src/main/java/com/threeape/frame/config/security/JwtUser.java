package com.threeape.frame.config.security;

import com.threeape.frame.entity.system.SysUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @Desc:
 * @Author: Bill
 * @Date: created in 19:58 2019/4/9
 * @Modified by:
 */
public class JwtUser implements UserDetails {

    private SysUser user;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean isEnable = true;

    public JwtUser(SysUser user, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public SysUser getUser() {
        return user;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
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
        return isEnable;
    }

    public static JwtUser.UserBuilder builder() {
        return new JwtUser.UserBuilder();
    }

    public static class UserBuilder {
        private SysUser user;
        private String username;
        private String password;
        private List<GrantedAuthority> authorities;

        private UserBuilder() {}

        public JwtUser.UserBuilder userDO(SysUser user) {
            Assert.notNull(user, "username cannot be null");
            this.user = user;
            return this;
        }

        public JwtUser.UserBuilder username(String username) {
            Assert.notNull(username, "username cannot be null");
            this.username = username;
            return this;
        }
        public JwtUser.UserBuilder password(String password) {
            Assert.notNull(password, "username cannot be null");
            this.password = password;
            return this;
        }
        public JwtUser.UserBuilder authorities(Collection<? extends GrantedAuthority> authorities) {
            this.authorities = new ArrayList(authorities);
            return this;
        }
        public UserDetails build() {
            return new JwtUser(user,username,password,authorities);
        }
    }
}
