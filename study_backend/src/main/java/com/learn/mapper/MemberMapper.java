package com.learn.mapper;

import com.learn.entity.TeamMember;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MemberMapper {
    //查全部
    @Select("select * from team_member")
    List<TeamMember> findAll();
    //根据名字查
    @Select("select * from team_member where name like CONCAT('%', #{name}, '%')")
    List<TeamMember> findByName(String name);

    @Select("select * from team_member where email =#{email}")
    TeamMember findByEmail(String email);
    //增
    @Insert("insert into team_member (name,address,date,email) values (#{name},#{address},#{date},#{email})")
    int insert(String name,String address,String date,String email);
    //改
    @Update("update team_member set name=#{newName},address=#{newAddress},date=#{newDate},email=#{newEmail} where email =#{oldEmail}")
    int update(String oldEmail,String newName,String newAddress,String newDate,String newEmail);
    //删
    @Delete("delete from team_member where email=#{email}")
    int delete(String email);

}
