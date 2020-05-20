package com.threeape.frame.config.security.config;

import com.threeape.frame.config.security.JwtUserService;
import com.threeape.frame.config.security.filter.JwtAuthenticationProvider;
import com.threeape.frame.config.security.filter.RequestFilter;
import com.threeape.frame.config.security.handler.JwtRefreshSuccessHandler;
import com.threeape.frame.config.security.handler.LoginSuccessHandler;
import com.threeape.frame.config.security.handler.TokenClearLogoutHandler;
import com.threeape.frame.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserService userService;
    @Autowired
    private LoginSuccessHandler loginSuccessHandler;
    @Autowired
    private JwtUserService jwtUserService;
    @Autowired
    private JwtRefreshSuccessHandler jwtRefreshSuccessHandler;
    @Autowired
    private TokenClearLogoutHandler tokenClearLogoutHandler;

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    protected AuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(jwtUserService);
    }

    @Bean
    protected AuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userDetailsService());
        return daoProvider;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .exceptionHandling()
                .and()
                .authorizeRequests()
                .antMatchers(permissiveUrl).permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().disable()
                .csrf().disable()
                .cors()
                .and()
                //拦截OPTIONS请求，直接返回header
                .addFilterAfter(new RequestFilter(), CorsFilter.class)
                //添加登录filter
                .apply(new LoginConfigurer<>(userService)).loginSuccessHandler(loginSuccessHandler)
                .and()
                //添加token的filter
                .apply(new JwtAccessConfigurer<>()).tokenValidSuccessHandler(jwtRefreshSuccessHandler)
                .permissiveRequestUrls(permissiveUrl)
                .and()
                //使用默认的logoutFilter
                .logout()
                //logout时清除token
                .addLogoutHandler(tokenClearLogoutHandler)
                //logout成功后返回200
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
    }

    @Override
    public void configure(WebSecurity web){
        //解决静态资源被拦截的问题
        web.ignoring().antMatchers("/static/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth
                .authenticationProvider(daoAuthenticationProvider())
                .authenticationProvider(jwtAuthenticationProvider());
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return jwtUserService;
    }

    private static final String[] permissiveUrl = new String[]{
            "/",
            "/sys/**",
            "/user/login",
            "/user/forgetPwd/**",
            "/user/modifyPwd",
            "/file/**",
            "/logout",
            "/login",
            "/verifyCode",
    };
}
