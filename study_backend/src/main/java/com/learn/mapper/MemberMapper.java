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

    @Select("select * from team_member where id =#{id}")
    TeamMember findById(int id);

    @Select("SELECT * FROM team_member WHERE star = 1")
    List<TeamMember> findStar();

    @Select("select * from team_member where email =#{email}")
    TeamMember findByEmail(String email);
    //增
    @Insert("insert into team_member (name,address,date,email,star) values (#{name},#{address},#{date},#{email},#{star})")
    void insert(String name,String address,String date,String email,boolean star);

    //改
    @Update("update team_member set name=#{newName},address=#{newAddress},date=#{newDate},email=#{newEmail},star=#{star} where email =#{oldEmail}")
    int update(String oldEmail,String newName,String newAddress,String newDate,String newEmail,boolean star);

    //删
    @Delete("delete from team_member where email=#{email}")
    int delete(String email);

    @Insert("INSERT INTO phones (member_id, phone) VALUES (#{memberId}, #{phone})")
    void addPhone(int memberId, String phone);

    // 修改手机号
    @Update("UPDATE phones SET phone = #{newPhone} WHERE member_id = #{memberId} AND phone = #{oldPhone}")
    void updatePhone(int memberId, String oldPhone, String newPhone);

    // 删除手机号
    @Delete("DELETE FROM phones WHERE member_id = #{memberId} AND phone = #{phone}")
    int deletePhone(int memberId, String phone);

    // 查询某成员的所有手机号
    @Select("SELECT phone FROM phones WHERE member_id = #{memberId}")
    List<String> findPhonesByMemberId(int memberId);
}
