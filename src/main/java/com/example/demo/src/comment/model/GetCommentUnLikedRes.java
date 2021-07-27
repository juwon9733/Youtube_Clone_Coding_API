package com.example.demo.src.comment.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class GetCommentUnLikedRes {
    private int commentLikeIdx;
    private int userIdx;
    private int commentIdx;
    private String sumOfUnLiked;
}
