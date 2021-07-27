package com.example.demo.src.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class GetCommentRes {
    private int Idx;
    private int videoIdx;
    private int postIdx;
    private int userIdx;
    private String comment;
}
