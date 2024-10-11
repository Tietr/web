package com.learn.service.Impl;
import com.learn.entity.Account;
import com.learn.mapper.UserMapper;
import com.learn.service.AuthorizeService;
import jakarta.annotation.Resource;
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
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service//权限校验服务
public class AuthorizeServiceImpl implements AuthorizeService {

    @Value("${spring.mail.username}")
    private String from;
    //引入Mapper
    @Resource
    UserMapper mapper;

    @Resource
    MailSender mailSender;

    @Resource
    StringRedisTemplate stringRedisTemplate;

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
    public boolean sendValidateEmail(String email ,String sessionId) {
        String Key = "email:" +sessionId+":"+ email;
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(Key))) {
            Long expire = Optional.ofNullable(stringRedisTemplate.getExpire(Key,TimeUnit.SECONDS)).orElse(0L);
            if (expire > 120 ) {
                return false;
            }
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

            return true;
        }catch (MailException e) {
            e.printStackTrace();
            return false;
        }
    }
}
