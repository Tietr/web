package com.learn.controller;
import com.learn.entity.RestBean;
import com.learn.service.AuthorizeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
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
    @Resource
    AuthorizeService authorizeService;

    @PostMapping("valid-email")
    public RestBean<String> validateEmail(@Pattern(regexp=EMAIL_REGEX) @RequestParam("email") String name,HttpSession session) {
        if(authorizeService.sendValidateEmail(name,session.getId())){
            return RestBean.success("邮件已发送，请注意查收");
        }else {
            return RestBean.failure(400,"邮箱发送失败，请联系管理");
        }
    }
}
