package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PostUserReq {
    private String imageUrl;
    private String name;
    private String birth;
    private String sex;
    private String passwd;
    private String email;
    private String phoneNumber;
    private String isKakaoLogin;
}
