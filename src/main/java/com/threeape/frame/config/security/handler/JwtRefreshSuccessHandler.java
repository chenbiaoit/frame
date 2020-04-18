package com.threeape.frame.config.security.handler;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.threeape.frame.config.security.JwtAuthenticationToken;
import com.threeape.frame.config.security.JwtUserService;
import com.threeape.frame.util.Constant;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @Desc:token刷新调度
 * @Author: Bill
 * @Date: created in 20:09 2019/4/20
 * @Modified by:
 */
@Component
public class JwtRefreshSuccessHandler implements AuthenticationSuccessHandler {

    @Resource
    private JwtUserService jwtUserService;

    //刷新间隔30分钟
    private static final int tokenRefreshInterval = 30;

    public JwtRefreshSuccessHandler(JwtUserService jwtUserService) {
        this.jwtUserService = jwtUserService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication)  {
        DecodedJWT jwt = ((JwtAuthenticationToken)authentication).getToken();
        boolean shouldRefresh = shouldTokenRefresh(jwt.getIssuedAt());
        if(shouldRefresh) {
            String newToken = jwtUserService.generateToken((UserDetails) authentication.getPrincipal());
            response.setHeader(Constant.Authorization, newToken);
        }
    }

    protected boolean shouldTokenRefresh(Date issueAt){
        LocalDateTime issueTime = LocalDateTime.ofInstant(issueAt.toInstant(), ZoneId.systemDefault());
        return LocalDateTime.now().minusMinutes(tokenRefreshInterval).isAfter(issueTime);
    }
}
