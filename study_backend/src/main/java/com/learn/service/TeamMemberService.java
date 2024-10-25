package com.learn.service;

import com.learn.entity.TeamMember;

import java.util.List;

public interface TeamMemberService {
    boolean updateMemberDetail(TeamMember newTeamMember);
    boolean deleteTeamMember(TeamMember teamMember);
    boolean addTeamMember(TeamMember teamMember);
    List<TeamMember> getTeamMember();
    TeamMember getTeamMemberByEmail(String email);
    List<TeamMember> getTeamMemberByName(String memberName);
}
