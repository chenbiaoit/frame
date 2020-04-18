package com.threeape.frame.config.security.config;

import com.threeape.frame.config.security.JwtUserService;
import com.threeape.frame.config.security.filter.JwtAuthenticationProvider;
import com.threeape.frame.config.security.filter.RequestFilter;
import com.threeape.frame.config.security.handler.JwtRefreshSuccessHandler;
import com.threeape.frame.config.security.handler.LoginSuccessHandler;
import com.threeape.frame.config.security.handler.TokenClearLogoutHandler;
import com.threeape.frame.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.kerberos.authentication.KerberosAuthenticationProvider;
import org.springframework.security.kerberos.authentication.KerberosServiceAuthenticationProvider;
import org.springframework.security.kerberos.authentication.sun.GlobalSunJaasKerberosConfig;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosClient;
import org.springframework.security.kerberos.authentication.sun.SunJaasKerberosTicketValidator;
import org.springframework.security.kerberos.web.authentication.SpnegoAuthenticationProcessingFilter;
import org.springframework.security.kerberos.web.authentication.SpnegoEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.Resource;

@EnableWebSecurity
@Configuration
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${app.kerberos.service-principal}")
    private String servicePrincipal;

    @Value("${app.kerberos.keytab-location}")
    private String keytabLocation;

    @Value("${app.kerberos.krb5-config}")
    private String krb5Config;

    @Resource
    private UserService userService;

    @Resource
    private LoginSuccessHandler loginSuccessHandler;
    @Resource
    private JwtUserService jwtUserService;
    @Resource
    private JwtRefreshSuccessHandler jwtRefreshSuccessHandler;
    @Resource
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
                .authenticationEntryPoint(spnegoEntryPoint())
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
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                .and()
                //添加kerberos spnego认证支持
                .addFilterBefore(spnegoAuthenticationProcessingFilter(authenticationManagerBean()), BasicAuthenticationFilter.class);
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
                .authenticationProvider(kerberosAuthenticationProvider())
                .authenticationProvider(kerberosServiceAuthenticationProvider())
                .authenticationProvider(jwtAuthenticationProvider());
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return jwtUserService;
    }

    @Bean
    public KerberosAuthenticationProvider kerberosAuthenticationProvider() {
        KerberosAuthenticationProvider provider = new KerberosAuthenticationProvider();
        SunJaasKerberosClient client = new SunJaasKerberosClient();
        client.setDebug(true);
        provider.setKerberosClient(client);
        provider.setUserDetailsService(userDetailsService());
        return provider;
    }

    @Bean
    public KerberosServiceAuthenticationProvider kerberosServiceAuthenticationProvider() {
        KerberosServiceAuthenticationProvider provider = new KerberosServiceAuthenticationProvider();
        provider.setTicketValidator(sunJaasKerberosTicketValidator());
        provider.setUserDetailsService(userDetailsService());
        return provider;
    }

    @Bean
    public SunJaasKerberosTicketValidator sunJaasKerberosTicketValidator() {
        final GlobalSunJaasKerberosConfig globalSunJaasKerberosConfig = new GlobalSunJaasKerberosConfig();
        globalSunJaasKerberosConfig.setKrbConfLocation(krb5Config);
        globalSunJaasKerberosConfig.setDebug(true);
        try {
            globalSunJaasKerberosConfig.afterPropertiesSet();
        } catch (Exception e) {
            log.error("设置krb5.ini({})文件出现异常",krb5Config);
        }
        SunJaasKerberosTicketValidator ticketValidator = new SunJaasKerberosTicketValidator();
        ticketValidator.setServicePrincipal(servicePrincipal);
        ticketValidator.setKeyTabLocation(new FileSystemResource(keytabLocation));
        ticketValidator.setDebug(true);
        return ticketValidator;
    }

    @Bean
    public SpnegoAuthenticationProcessingFilter spnegoAuthenticationProcessingFilter(
            AuthenticationManager authenticationManager) {
        SpnegoAuthenticationProcessingFilter filter = new SpnegoAuthenticationProcessingFilter();
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Bean
    public SpnegoEntryPoint spnegoEntryPoint() {
        return new SpnegoEntryPoint("/ad/forward");
    }

    private static final String[] permissiveUrl = new String[]{
            "/",
            "/sys/**",
            "/user/login",
            "/user/forgetPwd/**",
            "/user/modifyPwd",
            "/handover/deliver/data/**",
            "/file/**",
            "/logout",
            "/announcement/file/**",
            "/login",
            "/loginCheck",
            "/scheduleJob/**",
            "/ajax/scheduleJob/**",
            "/webservice/**",
            "/verifyCode",
            "/announcement/list",
            "/archive/list",
            "/user/dealer/**",
            "/user/ships/**"
    };
}
