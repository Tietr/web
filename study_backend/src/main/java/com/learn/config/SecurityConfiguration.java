package com.learn.config;


import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.learn.entity.RestBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

//配置，并添加网站验证？
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    //登录接口
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                //验证请求拦截和放行请求
                .authorizeHttpRequests( auth->{
                    auth.requestMatchers("/static/**").permitAll();//将所有静态资源放行，一定添加在全部请求拦截前
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
                    conf.logoutSuccessUrl("/login");
                    conf.permitAll();
                })
                .exceptionHandling(conf->{
                    conf.authenticationEntryPoint(this::onAuthenticationFailure);//无权限控制
                })
                .csrf(AbstractHttpConfigurer::disable)
                .build();

    }
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setCharacterEncoding("gbk");
        response.getWriter().write(JSONObject.toJSONString(RestBean.success("success")));
    }
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setCharacterEncoding("gbk");
        response.getWriter().write(JSONObject.toJSONString(RestBean.failure(401, exception.getMessage())));
    }

}
