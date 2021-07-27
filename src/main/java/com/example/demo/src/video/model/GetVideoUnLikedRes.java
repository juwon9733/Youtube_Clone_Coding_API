package com.example.demo.src.video.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class GetVideoUnLikedRes {
    private int userIdx;
    private int videoIdx;
    private String sumOfUnLiked;
}
