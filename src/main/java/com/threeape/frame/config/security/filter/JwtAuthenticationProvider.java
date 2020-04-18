package com.threeape.frame.config.security.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.threeape.frame.config.security.JwtAuthenticationToken;
import com.threeape.frame.config.security.JwtUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.NonceExpiredException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Desc:
 * @Author: Bill
 * @Date: created in 20:07 2019/4/20
 * @Modified by:
 */
@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private JwtUserService userService;

    private static final ThreadLocal threadLocal = new ThreadLocal() {
        protected synchronized Object initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    public JwtAuthenticationProvider(JwtUserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        DecodedJWT jwt = ((JwtAuthenticationToken)authentication).getToken();
        Date now = new Date();
        if(jwt.getExpiresAt().before(now)){
            log.info("Expiration time is {}",((SimpleDateFormat)threadLocal.get()).format(jwt.getExpiresAt()));
            log.info("Current time is {}",((SimpleDateFormat)threadLocal.get()).format(now));
            throw new NonceExpiredException("Token expires");
        }
        String username = jwt.getSubject();
        UserDetails user = userService.getUserLoginInfo(username);
        String encryptSalt = user.getPassword();
        try {
            Algorithm algorithm = Algorithm.HMAC256(encryptSalt);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withSubject(username)
                    .build();
            verifier.verify(jwt.getToken());
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid token", e);
        }

        //成功后返回认证信息，filter会将认证信息放入SecurityContext
        JwtAuthenticationToken token = new JwtAuthenticationToken(user, jwt, user.getAuthorities());
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(JwtAuthenticationToken.class);
    }

}
