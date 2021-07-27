package com.example.demo.src.kakaoLogin;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.utils.KakaoToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


import static com.example.demo.config.BaseResponseStatus.*;

import java.util.List;
@RestController
@RequestMapping("")
public class KakaoController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final RestTemplate restTemplate;
    @Autowired
    private final KakaoProvider kakaoProvider;
    @Autowired
    private final KakaoService kakaoService;
    @Autowired
    private final KakaoToken kakaoToken;

    public KakaoController(RestTemplate restTemplate, KakaoProvider kakaoProvider, KakaoService kakaoService, KakaoToken kakaoToken) {
        this.restTemplate = restTemplate;
        this.kakaoProvider = kakaoProvider;
        this.kakaoService = kakaoService;
        this.kakaoToken = kakaoToken;
    }

    /** [1].
     * 현재 사용자의 정보를 얻는 API
     * @return
     */
    @GetMapping("/app/kakao/now-user")
    public Object kakaoUserInfo() {
        String accessToken = kakaoToken.getKakaoToken();
        Object obj = kakaoProvider.kakaoUserInfo(accessToken);
        return obj;
    }


//    public BaseResponse<Object> kakaoUserInfo() throws BaseException {
//        String accessToken = kakaoToken.getKakaoToken();
//        Object obj = kakaoProvider.kakaoUserInfo(accessToken);
//        return new BaseResponse<>(obj);
//    }

}
