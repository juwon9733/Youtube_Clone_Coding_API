package com.example.demo.src.video.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class GetVideoCountCommentRes {
    private int videoIdx;
    private String countOfComment;
}
