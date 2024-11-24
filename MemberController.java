package com.learn.controller;
import com.learn.entity.RestBean;
import com.learn.entity.TeamMember;
import com.learn.service.TeamMemberService;
import jakarta.annotation.Resource;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/data")
public class MemberController {
    //
    @Resource
    TeamMemberService teamMemberService;
    //增加
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
    @PostMapping("post-phone")
    public RestBean<String> postNewPhoneData(
            @Valid @RequestParam("email") String email,
            @Valid @RequestParam("phone") String phone
    ){
        TeamMember member = teamMemberService.getTeamMemberByEmail(email);
        if(member == null){
            return RestBean.failure(300,"成员不存在");
        }else {

            teamMemberService.addPersonPhone(member.getId(),phone);
            return RestBean.success();
        }
    }
     //删减
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
    @PostMapping("del-phone")
    public RestBean<String> deletePhoneData(
            @Valid @RequestParam("email") String email,
            @Valid @RequestParam("phone") String phone
    ){
        TeamMember member = teamMemberService.getTeamMemberByEmail(email);
        if(member == null){
            return RestBean.failure(301,"该成员不存在");
        }else {
            if(teamMemberService.delMemberPhone(member.getId(),phone)){
                return RestBean.success("号码删除成功");
            }else {
                return RestBean.failure(301,"号码不存在");
            }
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

    @PostMapping("put-phone")
    public RestBean<String> putPhoneData(
            @Valid @RequestParam("email") String email,
            @Valid @RequestParam("oldPhone") String oldPhone,
            @Valid @RequestParam("newPhone") String newPhone
    ) {
        TeamMember member = teamMemberService.getTeamMemberByEmail(email);
        if (member == null) {
            return RestBean.failure(301, "该成员不存在");
        } else {
            if (teamMemberService.updateMemberPhone(member.getId(), oldPhone, newPhone)) {
                return RestBean.success("号码修改成功");
            } else {
                return RestBean.failure(302, "修改号码不存在");
            }
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
    @GetMapping("get-by-star")
    public RestBean<List<TeamMember>> getDataByStar(){
        return  RestBean.success(teamMemberService.getTeamMemberByStar());
    }

    @GetMapping("get-phone")
    public RestBean<List<String>> getPhoneById(
            @Valid @RequestParam("email") String email
            ){
            TeamMember member = teamMemberService.getTeamMemberByEmail(email);
            return RestBean.success(teamMemberService.getPhoneByTeamId(member.getId()));
    }


    @PostMapping("/import")
    public RestBean<String> importExcel(@RequestPart("file") MultipartFile file) {
        try {
            teamMemberService.importMembersFromExcel(file);
            return RestBean.success("Excel 导入成功");
        } catch (Exception e) {
            return RestBean.failure(500, "Excel 导入失败: " + e.getMessage());
        }
    }

    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=members.xlsx");
            teamMemberService.exportMembersToExcel(response.getOutputStream());
        } catch (Exception e) {
            throw new RuntimeException("Excel 导出失败: " + e.getMessage());
        }
    }

}
