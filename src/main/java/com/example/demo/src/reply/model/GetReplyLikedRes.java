package com.example.demo.src.reply.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class GetReplyLikedRes {
    private int replyLikeIdx;
    private int userIdx;
    private int replyIdx;
    private String sumOfLiked;
}
