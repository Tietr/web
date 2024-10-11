package com.learn.config;


import com.alibaba.fastjson.JSONObject;
import com.learn.entity.RestBean;
import com.learn.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.io.IOException;

//配置，并添加网站验证？
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Resource
    AuthorizeService authorizeService;

    @Resource
    DataSource dataSource;

    //登录接口
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, PersistentTokenRepository repository) throws Exception {
        return http
                //验证请求拦截和放行请求
                .authorizeHttpRequests( auth->{
                    auth.requestMatchers("/static/**","/api/auth/**").permitAll();//将所有静态资源放行，一定添加在全部请求拦截前
                    auth.anyRequest().authenticated();//所有资源请求全部拦截，一律需要验证
                })
                .formLogin( conf->{
//                    conf.loginPage("/login");//将登录界面设置成自己的登录界面
                    conf.loginProcessingUrl("/api/auth/login");//登录表单提交的地址，可自定义,用于处理登录
                    conf.defaultSuccessUrl("/");//登录成功后的
                    conf.successHandler(this::onAuthenticationSuccess);
                    conf.failureHandler(this::onAuthenticationFailure);
                    conf.permitAll();//单独设置登录界面为所有可用
                    //其他设置
                    conf.usernameParameter("username");
                    conf.passwordParameter("password");
                })
                .logout( conf ->{
                    conf.logoutUrl("/api/auth/logout");
                    conf.logoutSuccessUrl("/");
                    conf.logoutSuccessHandler(this::onAuthenticationSuccess);
//                    conf.logoutSuccessUrl("/login");
                    conf.permitAll();
                })
                .rememberMe( conf->{
                    conf.rememberMeParameter("remember");
                    conf.tokenRepository(repository);
                    //7天免输入登录
                    conf.tokenValiditySeconds(60*60*24*7);
                })
                .userDetailsService(authorizeService)//或者下面再加个bean，但是教程中的写法，在该版本不支持
                .exceptionHandling(conf->{
                    conf.authenticationEntryPoint(this::onAuthenticationFailure);//无权限控制
                })
                .csrf(AbstractHttpConfigurer::disable)
                .cors( conf-> {
                    conf.configurationSource(this.corsConfigurationSource());
                })
                .build();

    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        //持续化存储Token
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
//        jdbcTokenRepository.setCreateTableOnStartup(true); --创建这个表后就可以取消了
        jdbcTokenRepository.setCreateTableOnStartup(false);
        return jdbcTokenRepository;
    }

//    解决跨域的报错问题,可能需要修改
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.addAllowedOriginPattern(/*"http://localhost:3000"*/"*");//waring：这里写星号非常不安全，之后需要修改
        cors.setAllowCredentials(true);
        cors.addAllowedHeader("*");
        cors.addAllowedMethod("*");
        cors.addExposedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        response.setCharacterEncoding("utf-8");
        if(request.getRequestURI().contains("/login")) {
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("登录成功")));
        }else if(request.getRequestURI().contains("/logout")) {
            response.getWriter().write(JSONObject.toJSONString(RestBean.success("退出登录成功")));
        }
    }
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401, exception.getMessage())));
    }

}
