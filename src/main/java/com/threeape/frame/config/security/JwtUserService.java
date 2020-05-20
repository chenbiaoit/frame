package com.threeape.frame.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.threeape.frame.entity.system.SysRole;
import com.threeape.frame.entity.system.SysUser;
import com.threeape.frame.repository.RoleRepository;
import com.threeape.frame.repository.UserRepository;
import com.threeape.frame.util.DateUtil;
import com.threeape.frame.util.Enums;
import com.threeape.frame.util.PasswordHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

/**
 * @Desc:
 * @Author: Bill
 * @Date: created in 00:02 2019/4/20
 * @Modified by:
 */
@Component
@Slf4j
public class JwtUserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private final static String secret = "ioiuffkII#022";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, LockedException {
        SysUser user = userRepository.findByLoginName(username);
        if (Objects.isNull(user)) {
            String errorMsg = String.format("No user found with username '%s'", username);
            log.error(errorMsg);
            throw new UsernameNotFoundException(errorMsg);
        }
        if(user.getUserStatus().equals(0)){
            throw new LockedException("locked");
        }
        if(user.getPwdInvalidTime().before(new Date())){
            throw new LockedException("password expiration");
        }
        SysRole sysrole = user.getSysRole();
        if(Objects.isNull(sysrole)){
            throw new LockedException("Role-free user");
        }
        return new JwtUser(user,user.getLoginName(),user.getLoginPwd(),
                Collections.singleton(new SimpleGrantedAuthority(sysrole.getRoleCode())));
    }

    /**
     * 生成token
     * @param user
     * @return
     */
    public String generateToken(UserDetails user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        //设置一个小时后过期
        Date date = new Date(System.currentTimeMillis()+ 60 * 60 * 1000);
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(date)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    /**
     * 清除数据库或者缓存中登录secret
     */
    public void deleteUserLoginInfo(String username) {
        log.info("用户{}完成退出",username);
    }

    public UserDetails getUserLoginInfo(String username) {
        JwtUser user = (JwtUser) loadUserByUsername(username);
        return JwtUser.builder()
                .userDO(user.getUser())
                .username(user.getUsername())
                .password(secret)
                .authorities(user.getAuthorities())
                .build();
    }

    private String getLoginName(String loginName) {
        if(userRepository.findByLoginName(loginName) == null){
            return loginName;
        }else{
            loginName = loginName + creatRandom(4);
            return getLoginName(loginName);
        }
    }

    public static String creatRandom(int size) {

        if (size <= 0) {
            size = 1;
        }
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < size; i++) {
            sb.append(new Random().nextInt(10));
        }
        return sb.toString();

    }
}
