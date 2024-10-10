package com.learn.service;
import com.learn.entity.Account;
import com.learn.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Service//权限校验服务
public class AuthorizeService implements UserDetailsService {

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
}
