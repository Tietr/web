package com.learn.service.Impl;

import com.learn.entity.TeamMember;
import com.learn.mapper.MemberMapper;
import com.learn.service.TeamMemberService;
import jakarta.annotation.Resource;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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
        return memberMapper.update(newTeamMember.getOldEmail(),newTeamMember.getName(), newTeamMember.getAddress(),
                newTeamMember.getDate(),newTeamMember.getEmail()) > 0;
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



    @Override
    public List<TeamMember> getTeamMemberByStar(){
        return memberMapper.findStar();
    }



    @Override
    public boolean addPersonPhone(int id,String Phone){
        if(getPhoneByTeamId(id) == null){
            return false;
        }else{
            memberMapper.addPhone(id,Phone);
            return true;
        }
    }

    @Override
    public boolean updateMemberPhone(int id,String oldPhone,String newPhone){
        if(getPhoneByTeamId(id) == null){
            return false;
        }else{
            if (getPhoneByTeamId(id).equals(oldPhone)) {
            memberMapper.updatePhone(id,oldPhone,newPhone);
            return true;
            }else {
                return false;
            }
        }
    }

    @Override
    public boolean delMemberPhone(int id ,String phone){
        if(getPhoneByTeamId(id)==null){
            return false;
        }else {
            if (getPhoneByTeamId(id).equals(phone)) {
                memberMapper.deletePhone(id,phone);
                return true;
            }else {
                return false;
            }
        }
    }

    @Override
    public List<String> getPhoneByTeamId(int id){
        return memberMapper.findPhonesByMemberId(id);
    }



    @Override
    public void importMembersFromExcel(MultipartFile file) throws Exception {
        try (InputStream inputStream = file.getInputStream()) {
            // 读取 Excel 文件
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            List<TeamMember> members = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 从第1行开始，跳过标题
                Row row = sheet.getRow(i);

                if (row == null) continue; // 空行跳过

                // 获取每列数据
                String name = row.getCell(0).getStringCellValue();
                String email = row.getCell(1).getStringCellValue();
                String address = row.getCell(2).getStringCellValue();
                String date = row.getCell(3).getStringCellValue();
                boolean star = row.getCell(4).getBooleanCellValue();

                // 解析手机号列表
                String phoneList = row.getCell(5).getStringCellValue(); // 逗号分隔
                String[] phones = phoneList.split(",");

                // 创建成员对象
                TeamMember member = new TeamMember();
                member.setName(name);
                member.setEmail(email);
                member.setAddress(address);
                member.setDate(date);
                member.setStar(star);

                // 保存成员信息
                addTeamMember(member); // 假设该方法会检查重复的成员并抛出异常
                TeamMember savedMember = getTeamMemberByEmail(email);
                if (savedMember == null) {
                    throw new Exception("无法通过 email 找到插入的成员记录：" + email);
                }
                for (String phone : phones) {
                    addPersonPhone(savedMember.getId(), phone.trim());
                }
            }
        } catch (IOException e) {
            throw new Exception("文件读取失败", e);
        } catch (Exception e) {
            throw new Exception("Excel 数据导入失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void exportMembersToExcel(OutputStream outputStream) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Team Members");

            // 创建标题行
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Name");
            header.createCell(1).setCellValue("Email");
            header.createCell(2).setCellValue("Phones");

            // 获取所有成员及其电话号码
            List<TeamMember> members = getTeamMember();
            int rowIndex = 1;

            for (TeamMember member : members) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(member.getName());
                row.createCell(1).setCellValue(member.getEmail());

                // 拼接电话号码
                List<String> phones = getPhoneByTeamId(member.getId());
                String phoneList = String.join(", ", phones);
                row.createCell(2).setCellValue(phoneList);
            }

            // 自动调整列宽
            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            // 写入到输出流
            workbook.write(outputStream);
        } catch (IOException e) {
            throw new Exception("Excel 数据导出失败", e);
        }
    }



}
