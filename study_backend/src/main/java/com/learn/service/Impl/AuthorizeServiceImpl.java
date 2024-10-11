package com.learn.service.Impl;
import com.learn.entity.Account;
import com.learn.mapper.UserMapper;
import com.learn.service.AuthorizeService;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Service//权限校验服务
public class AuthorizeServiceImpl implements AuthorizeService {

    //引入Mapper
    @Resource
    UserMapper mapper;

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

    @Override
    public boolean sendValidateEmail(String email) {
/**
 * 1.先生成对应验证码
 * 2.将邮箱和验证码直接放在Redis中，（过期时间3分钟，如果此时重新要求发送邮件，若剩余时间低于两分钟，则重新发送）
 * 3.发送验证码到指定邮箱
 * 4.如果发送失败，把Redis中的插入的键值对删除
 * 5.用户在注册时，再从Redis中取出对应键值对，匹配验证码
 */

        return false;
    }
}
