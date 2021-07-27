package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserRes {
    private String imageUrl;
    private String name;
    private String birth;
    private String sex;
    private String passwd;
    private String email;
    private String phoneNumber;
    private String status;
}
