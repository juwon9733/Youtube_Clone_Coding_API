package com.example.demo.src.video.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetVideoPlayListRes {
    private int userIdx;
    private String videoUrl;
    private String title;
    private String videoExplain;
    private String videoTime;
}
