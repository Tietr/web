package com.learn.service;

import com.learn.entity.TeamMember;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
import java.util.List;

public interface TeamMemberService {
    boolean updateMemberDetail(TeamMember newTeamMember);
    boolean deleteTeamMember(TeamMember teamMember);
    boolean addTeamMember(TeamMember teamMember);
    List<TeamMember> getTeamMember();
    TeamMember getTeamMemberByEmail(String email);
    List<TeamMember> getTeamMemberByName(String memberName);
    List<TeamMember> getTeamMemberByStar();
    boolean addPersonPhone(int id,String Phone);
    //查找
    boolean updateMemberPhone(int id,String oldPhone,String newPhone);
    List<String> getPhoneByTeamId(int id);
    boolean delMemberPhone(int id ,String phone);
    void importMembersFromExcel(MultipartFile file) throws Exception;
    void exportMembersToExcel(OutputStream outputStream) throws Exception;
}
