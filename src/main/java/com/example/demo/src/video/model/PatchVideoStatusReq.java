package com.example.demo.src.video.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchVideoStatusReq {
    private int videoIdx;
    private int userIdx;
    private String status;
}
