package com.learn.service.Impl;
import com.learn.entity.Account;
import com.learn.mapper.UserMapper;
import com.learn.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.websocket.Session;
import lombok.extern.java.Log;
import org.jetbrains.annotations.Debug;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service//权限校验服务
public class AuthorizeServiceImpl implements AuthorizeService {


    //引入Mapper
    //用户数据库
    @Resource
    UserMapper mapper;

    @Value("${spring.mail.username}")
    private String from;
    @Resource
    MailSender mailSender;

    //用户注册ID-验证码临时存储
    @Resource
    StringRedisTemplate stringRedisTemplate;

//Bean好像是注册静态资源，而Resource是取这个资源
//hashcode
//    @Resource
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    //数据库取数据,验证用户
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return null;
        if (username == null) {
            throw new UsernameNotFoundException("用户名为空");
        }
        Account account = mapper.findAccountByNameOrEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .roles("user")
                .build();
    }
    /**
     * 1.先生成对应验证码
     * 3.发送验证码到指定邮箱
     * 2.将邮箱和验证码直接放在Redis中，（过期时间3分钟，如果此时重新要求发送邮件，若剩余时间低于两分钟，则重新发送）
     * 4.如果发送失败，把Redis中的插入的键值对删除
     * 5.用户在注册时，再从Redis中取出对应键值对，匹配验证码
     */
    @Override
    public String sendValidateEmail(String email ,String sessionId) {
        String Key = "email:" +sessionId+":"+ email;
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(Key))) {
            Long expire = Optional.ofNullable(stringRedisTemplate.getExpire(Key,TimeUnit.SECONDS)).orElse(0L);
            if (expire > 120 ) {
                return "请求频繁，请稍后再试";
            }
        }
        //如果存在了用户存在
        if(mapper.findAccountByNameOrEmail(email) != null) {
            return "此邮箱已被其他用户注册";
        }

        Random random = new Random();
        int code = random.nextInt(899999) + 100000;
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(email);
        message.setSubject("您的验证邮件");
        message.setText("您的验证码是："+ code);
        try {
            mailSender.send(message);

            //存入redis
            stringRedisTemplate.opsForValue().set(Key,String.valueOf(code),3, TimeUnit.MINUTES);

            return null;
        }catch (MailException e) {
            e.printStackTrace();
            return "邮件发送失败，请检查邮件是否有效";
        }
    }

    @Override
    public String validateAndRegister(String username, String password, String email, String code, String sessionId) {
        String Key = "email:" + sessionId+":"+ email;
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(Key))) {
            String s = stringRedisTemplate.opsForValue().get(Key);
            if (s==null) {
                return "验证码失效，请重新获取验证码";
            }
            if (s.equals(code)) {
                password = encoder.encode(password);//hash
                if( mapper.createAccount(username, password, email) >0){
                    return null;
                }else {
                    return "内部错误，请联系管理员";
                }

            }else {
                return "验证码错误,请检查后再提交";
            }
        }else {
            return "请先获取验证码邮件";
        }

    }

}
