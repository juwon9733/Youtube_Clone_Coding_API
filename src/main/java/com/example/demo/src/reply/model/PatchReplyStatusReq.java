package com.example.demo.src.reply.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class PatchReplyStatusReq {
    private int replyIdx;
    private int userIdx;
    private String status;
}
