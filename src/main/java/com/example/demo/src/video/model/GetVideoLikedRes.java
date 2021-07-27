package com.example.demo.src.video.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class GetVideoLikedRes {
    private int userIdx;
    private int videoIx;
    private String sumOfLiked;
}
