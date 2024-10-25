package com.learn.service.Impl;

import com.learn.entity.TeamMember;
import com.learn.mapper.MemberMapper;
import com.learn.service.TeamMemberService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamMemberServiceImpl implements TeamMemberService {
    @Resource
    MemberMapper memberMapper;

    @Override
    public boolean updateMemberDetail(TeamMember newTeamMember) {
        TeamMember t = getTeamMemberByEmail(newTeamMember.getOldEmail());
        if (t == null) {
            return false;
        }
        return memberMapper.update(newTeamMember.getOldEmail(),newTeamMember.getName(), newTeamMember.getAddress(), newTeamMember.getDate(),newTeamMember.getEmail()) > 0;

    }

    @Override
    public boolean deleteTeamMember(TeamMember teamMember) {
        memberMapper.delete(teamMember.getEmail());
        return true;
    }

    @Override
    public boolean addTeamMember(TeamMember teamMember) {
        memberMapper.insert(teamMember.getName(), teamMember.getAddress(), teamMember.getDate(), teamMember.getEmail());
        return true;
    }

    @Override
    public List<TeamMember> getTeamMember() {
        return memberMapper.findAll();
    }

    @Override
    public TeamMember getTeamMemberByEmail(String email) {
        memberMapper.findByEmail(email);
        return memberMapper.findByEmail(email);
    }

    @Override
    public List<TeamMember> getTeamMemberByName(String memberName) {
        return memberMapper.findByName(memberName);
    }
}
