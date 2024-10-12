package com.learn.controller;
import com.learn.entity.RestBean;
import com.learn.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/auth")//请求相关
public class AuthorizeController {
    private final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$";
    private final String USERNAME_REGEX = "[一-龥a-zA-Z0-9]+";
    private final String PASSWORD_REGEX = "^[^一-龥]+$";
    @Resource
    AuthorizeService authorizeService;

    @PostMapping("valid-email")
    public RestBean<String> validateEmail(@Pattern(regexp=EMAIL_REGEX) @RequestParam("email") String name,HttpSession session) {

        String s = authorizeService.sendValidateEmail(name,session.getId());
        if(s == null){
            return RestBean.success("邮件已发送，请注意查收");
        }else {
            return RestBean.failure(400,s);
        }
    }

    @PostMapping("/register")
    public RestBean<String> registerUser(
            @Pattern(regexp= USERNAME_REGEX) @Length(min = 2,max = 8) @RequestParam("username") String username,
            @Pattern(regexp = PASSWORD_REGEX) @Length(min = 6,max = 20) @RequestParam("password") String password,
            @Pattern(regexp = EMAIL_REGEX) @RequestParam("email") String email,
            @Length(min = 6,max = 6) @RequestParam("code") String code,
            HttpSession session) {
        String s = authorizeService.validateAndRegister(username,password,email,code,session.getId());
        if (s == null) {
            return RestBean.success("用户注册成功");
        }else {
            return RestBean.failure(400,s);
        }

    }


}
