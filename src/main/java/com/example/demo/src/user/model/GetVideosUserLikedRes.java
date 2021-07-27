package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetVideosUserLikedRes {
    private String videoUrl;
    private String title;
    private String videoExplain;
    private String VideoTime;
    private String kind;
}
