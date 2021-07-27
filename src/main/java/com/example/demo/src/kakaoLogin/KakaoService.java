package com.example.demo.src.kakaoLogin;

import com.example.demo.utils.KakaoToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KakaoService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final KakaoProvider kakaoProvider;
    @Autowired
    private final KakaoToken kakaoToken;

    public KakaoService(KakaoProvider kakaoProvider, KakaoToken kakaoToken) {
        this.kakaoProvider = kakaoProvider;
        this.kakaoToken = kakaoToken;
    }
}
