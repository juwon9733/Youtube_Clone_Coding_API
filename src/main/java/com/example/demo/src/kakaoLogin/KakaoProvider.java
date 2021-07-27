package com.example.demo.src.kakaoLogin;

import com.example.demo.src.user.model.KakaoProfile;
import com.example.demo.utils.KakaoToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoProvider{

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final KakaoToken kakaoToken;
    @Autowired
    private final RestTemplate restTemplate;

    @Autowired
    public KakaoProvider(KakaoToken kakaoToken, RestTemplate restTemplate) {
        this.kakaoToken = kakaoToken;
        this.restTemplate = restTemplate;
    }
    public Object kakaoUserInfo(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", kakaoToken.getKakaoToken());
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("grant_type", "authorization_code");

//        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(params, headers);
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                kakaoProfileRequest,
                String.class
        );
        System.out.println(response.getBody());

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper.readValue(response.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println("카카오 아이디" + kakaoProfile.getId());
        System.out.println("카카오 이메일" + kakaoProfile.getKakao_account().getEmail());

        System.out.println("어플 아이디" + kakaoProfile.getId());
        System.out.println("어플 이메일" + kakaoProfile.getKakao_account().getEmail());
        return response.getBody();
    }
}
