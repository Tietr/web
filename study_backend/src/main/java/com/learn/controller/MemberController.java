package com.learn.controller;
import com.learn.entity.RestBean;
import com.learn.entity.TeamMember;
import com.learn.service.TeamMemberService;
import jakarta.annotation.Resource;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/data")
public class MemberController {
    //具体实现
    @Resource
    TeamMemberService teamMemberService;
    //增
    @PostMapping("/post")
    public RestBean<String> postNewData(
        @Valid @ModelAttribute TeamMember teamMember
    ){
        TeamMember member = teamMemberService.getTeamMemberByEmail(teamMember.getEmail());
        if(member == null){
            teamMemberService.addTeamMember(teamMember);
            return RestBean.success("添加成功");
        }else{
            return RestBean.failure(300,"该邮箱已有成员拥有");
        }
    }
     //删
    @PostMapping("/del")
    public RestBean<String> deleteData(
            @Valid @RequestParam("email") String email
    ){
        TeamMember member = teamMemberService.getTeamMemberByEmail(email);
        if(member == null){
            return RestBean.failure(301,"该成员不存在");
        }else{
            teamMemberService.deleteTeamMember(member);
            return RestBean.success("删除成功");
        }
    }
    //改
    @PostMapping("/put")
    public RestBean<String> putData(
            @Valid @ModelAttribute TeamMember Data
    ){
        if(!teamMemberService.updateMemberDetail(Data)){
            return RestBean.failure(302,"修改的成员不存在");
        }else{
            return RestBean.success("修改成功");
        }
    }
    //查
    @GetMapping("/get")
    public RestBean<List<TeamMember>> getDataAll(){
        return RestBean.success(teamMemberService.getTeamMember());
    }
    @GetMapping("get-by-name")
    public RestBean<List<TeamMember>> getDataByName(
            @RequestParam("name") String name
    ){
        List<TeamMember> members = teamMemberService.getTeamMemberByName(name);
        return RestBean.success(members);
    }
}
