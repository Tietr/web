package com.learn.entity;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
@Data
public class TeamMember {
    private int id;
    String name;
    String address;
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Invalid email format")
    String email;
    String date;
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Invalid email format")
    String oldEmail;
    public TeamMember(String name, String address, String email, String date) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.date = date;
    }
    public TeamMember() {}
}
