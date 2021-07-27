package com.example.demo.src.reply.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class GetReplyUnLikedRes {
    private int replyUnLikeIdx;
    private int userIdx;
    private int replyIdx;
    private String sumOfUnLiked;
}
