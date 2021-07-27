package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostVideoHistoryReq {
    private int userIdx;
    private int videoIdx;
    private String playTime;
}
