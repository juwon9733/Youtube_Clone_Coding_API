package com.example.demo.src.user;

import com.example.demo.utils.KakaoToken;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/app/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final RestTemplate restTemplate;
    @Autowired
    private final KakaoToken kakaoToken;

//    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService, RestTemplate restTemplate, KakaoToken kakaoToken) {
//        this.userProvider = userProvider;
//        this.userService = userService;
//        this.jwtService = jwtService;
//        this.restTemplate = restTemplate;
//        this.kakaoToken = kakaoToken;
//    }

    /**
     * [1].
     * 전체 유저 조회 API
     * [GET] /users
     * + 이메일로 특정 유저 조회 API
     * [GET] /users? Email=
     * @return BaseResponse<List < GetUserRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String Email) {
        try {
            if (Email == null) {
                List<GetUserRes> getUsersRes = userProvider.getUsers();
                return new BaseResponse<>(getUsersRes);
            } else if (!isRegexEmail(Email)) {
                return new BaseResponse<>(INVALID_USER_EMAIL);
            }
            List<GetUserRes> getUsersRes = userProvider.getUsersByEmail(Email);
            return new BaseResponse<>(getUsersRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * [2].
     * 특정 유저 조회 API
     * [GET] /users/:userIdx
     * @return BaseResponse<GetUserRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<GetUserRes> getUserByUserIdx(@PathVariable("userIdx") int userIdx) {
        try {
            GetUserRes getUserRes = userProvider.getUserByUserIdx(userIdx);
            return new BaseResponse<>(getUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * [3].
     * 회원가입(유저 생성) API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        if (postUserReq.getName() == null) {
            return new BaseResponse<>(EMPTY_USER_NAME);
        } else if (postUserReq.getName().length() >= 10) {
            return new BaseResponse<>(TOO_LONG_USER_NAME);
        }
        if (postUserReq.getBirth() == null) {
            return new BaseResponse<>(EMPTY_USER_BIRTH);
        } else if (!isRegexBirth(postUserReq.getBirth())) {
            return new BaseResponse<>(INVALID_USER_BIRTH);
        }
        if (postUserReq.getSex() == null) {
            return new BaseResponse<>(EMPTY_USER_SEX);
        } else if (postUserReq.getSex().equals("M") == false && postUserReq.getSex().equals("F") == false) {
            return new BaseResponse<>(INVALID_USER_SEX);
        }
        if (postUserReq.getPasswd() == null) {
            return new BaseResponse<>(EMPTY_USER_PASSWD);
        }
        if (postUserReq.getPhoneNumber() == null) {
            return new BaseResponse<>(EMPTY_USER_PHONE_NUM);
        } else if (!isRegexPhoneNumber(postUserReq.getPhoneNumber())) {
            return new BaseResponse<>(INVALID_USER_PHONE_NUM);
        }
        if (postUserReq.getEmail() == null) {
            return new BaseResponse<>(EMPTY_USER_EMAIL);
        } else if (!isRegexEmail(postUserReq.getEmail())) {
            return new BaseResponse<>(INVALID_USER_EMAIL);
        }
        if (postUserReq.getIsKakaoLogin() == null) {
            return new BaseResponse<>(EMPYT_KAKAO_LOGIN);
        }
        try {
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [4].
     * 카카오 회원가입 API
     * [POST] /users/kakao/sign-in
     * @return BaseResponse<PostUserRes>
     */
    @PostMapping("/kakao/sign-in")
    public BaseResponse<PostUserRes> createUserByKakaoLogin(@RequestBody KakaoLoginReq kakaoLoginReq) {
        if (kakaoLoginReq.getName() == null) {
            return new BaseResponse<>(EMPTY_USER_NAME);
        } else if (kakaoLoginReq.getName().length() >= 10) {
            return new BaseResponse<>(TOO_LONG_USER_NAME);
        }
        if (kakaoLoginReq.getBirth() == null) {
            return new BaseResponse<>(EMPTY_USER_BIRTH);
        } else if (!isRegexBirth(kakaoLoginReq.getBirth())) {
            return new BaseResponse<>(INVALID_USER_BIRTH);
        }
        if (kakaoLoginReq.getSex() == null) {
            return new BaseResponse<>(EMPTY_USER_SEX);
        } else if (kakaoLoginReq.getSex().equals("M") == false && kakaoLoginReq.getSex().equals("F") == false) {
            return new BaseResponse<>(INVALID_USER_SEX);
        }
        if (kakaoLoginReq.getPhoneNumber() == null) {
            return new BaseResponse<>(EMPTY_USER_PHONE_NUM);
        } else if (!isRegexPhoneNumber(kakaoLoginReq.getPhoneNumber())) {
            return new BaseResponse<>(INVALID_USER_PHONE_NUM);
        }
        if (kakaoLoginReq.getIsKakaoLogin().equals("Y") == false) {
            return new BaseResponse<>(IVALID_KAKAO_LOGIN);
        }
        ////////////////////////////////////////////////////////////////////////////////
        String accessToken = kakaoToken.getKakaoToken();
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
//        System.out.println(response.getBody());

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper.readValue(response.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println("카카오 아이디" + kakaoProfile.getId());
        System.out.println("카카오 이메일" + kakaoProfile.getKakao_account().getEmail());

        PostUserReq postUserReq = PostUserReq.builder()
                .name(kakaoLoginReq.getName())
                .birth(kakaoLoginReq.getBirth())
                .sex(kakaoLoginReq.getSex())
                .passwd("hard_coding")
                .email(kakaoProfile.getKakao_account().getEmail())
                .phoneNumber(kakaoLoginReq.getPhoneNumber())
                .isKakaoLogin(kakaoLoginReq.getIsKakaoLogin())
                .build();

        try {
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
//        return new BaseResponse<>(response.getBody());
    }
    /**
     * [5].
     * 로그인 API
     * [POST] /users/login
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        if (postLoginReq.getEmail() == null) {
            return new BaseResponse<>(EMPTY_USER_EMAIL);
        } else if (!isRegexEmail(postLoginReq.getEmail())) {
            return new BaseResponse<>(INVALID_USER_EMAIL);
        }
        if (postLoginReq.getPasswd() == null) {
            return new BaseResponse<>(EMPTY_USER_PASSWD);
        }
        try {
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * [6]
     * 카카오 로그인 API
     * [Post] /users/kakao/login
     * @return BaseResponse<PostLoginRes>
     */
    @PostMapping("/kakao/login")
    public BaseResponse<PostLoginRes> kakaoLogin() {
        String accessToken = kakaoToken.getKakaoToken();
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

        PostLoginReq postLoginReq = PostLoginReq.builder()
                .email(kakaoProfile.getKakao_account().getEmail())
                .passwd("hard_coding")
                .build();

        try {
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [5].
     * 로그아웃 API
     * [POST] /users/logout
     * @return BaseResponse<PostLogoutRes>
     */
    @ResponseBody
    @PostMapping("/logout")
    public BaseResponse<PostLogoutRes> logOut() {
        try {
            String userJwtToken = jwtService.getJwt();
            PostLogoutRes postLogoutRes = userService.logOut(userJwtToken);
            return new BaseResponse<>(postLogoutRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [7].
     * 특정 유저가 자신의 유저 정보 변경 API
     * 유저 정보 변경은 개인만 가능하다. -> Headers에 X-ACCESS-TOKEN을 같이 보내야한다.
     * [PATCH] /users/:userIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}")
    //Headers에 X-ACCESS-TOKEN을 같이 보내야한다. X-ACCESS-TOKEN은 user로그인시 return 되는 jwt값. 이 토큰이 있는것 자체를 로그인 했다고 생각하고 하는듯?
    public BaseResponse<String> modifyUserInfo(@PathVariable("userIdx") int userIdx, @RequestBody PatchModifyUserReq patchModifyUserReq) throws BaseException {
        if(userProvider.checkDeltedToken(jwtService.getJwt()) == true) {
            return new BaseResponse<>(DELETED_TOKEN);
        }
        if (userIdx == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        if (patchModifyUserReq.getName() != null) {
            if (patchModifyUserReq.getName().length() >= 10) {
                return new BaseResponse<>(TOO_LONG_USER_NAME);
            }
        }
        if (patchModifyUserReq.getBirth() != null) {
            if (!isRegexBirth(patchModifyUserReq.getBirth())) {
                return new BaseResponse<>(INVALID_USER_BIRTH);
            }
        }
        if(patchModifyUserReq.getSex() != null) {
            if (patchModifyUserReq.getSex().equals("M") == false && patchModifyUserReq.getSex().equals("F") == false) {
                return new BaseResponse<>(INVALID_USER_SEX);
            }
        }
        if(patchModifyUserReq.getPhoneNumber() != null) {
            if (!isRegexPhoneNumber(patchModifyUserReq.getPhoneNumber())) {
                return new BaseResponse<>(INVALID_USER_PHONE_NUM);
            }
        }
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PatchUserReq patchUserReq = new PatchUserReq(userIdx, patchModifyUserReq.getImageUrl(), patchModifyUserReq.getName(),
                    patchModifyUserReq.getBirth(), patchModifyUserReq.getSex(), patchModifyUserReq.getPasswd(),
                    patchModifyUserReq.getPhoneNumber());
            userService.modifyUserInfo(patchUserReq);

            String result = "User Info modified";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [8].
     * 특정 유저가 자신의 상태 변경(활성화, 비활성화) API
     * [PATCH] /users/status/:userIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/status/{userIdx}")
    //Headers에 X-ACCESS-TOKEN을 같이 보내야한다. X-ACCESS-TOKEN은 user로그인시 return 되는 jwt값. 이 토큰이 있는것 자체를 로그인 했다고 생각하고 하는듯?
    public BaseResponse<String> modifyUserStatusInfo(@PathVariable("userIdx") int userIdx, @RequestBody PatchModifyUserStatusReq patchModifyUserStatusReq) {
        if (userIdx == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        if (patchModifyUserStatusReq.getStatus() == null) {
            return new BaseResponse<>(EMPTY_USER_STATUS);
        }
        else if (patchModifyUserStatusReq.getStatus().equals("Y") == false && patchModifyUserStatusReq.getStatus().equals("N") == false) {
            return new BaseResponse<>(INVALID_USER_STATUS);
        }

        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            patchModifyUserStatusReq = new PatchModifyUserStatusReq(userIdx, patchModifyUserStatusReq.getStatus());
            userService.modifyUserStatusInfo(patchModifyUserStatusReq);

            String result = "User Status modified";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * [9].
     * 특정 유저의 구독 채널 조회 API
     * [GET] /users/subscribes/:userIdx
     * @return BaseResponse<GetUserRes>
     */
    @ResponseBody
    @GetMapping("/subscribes/{userIdx}")
    public BaseResponse<List<GetUserRes>> getUserSubs(@PathVariable("userIdx") int userIdx) {
        if (userIdx == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        try {
            List<GetUserRes> getUserRes = userProvider.getUserSubs(userIdx);
            return new BaseResponse<>(getUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [10].
     * 특정 유저가 자신의 검색어 기록 조회 API
     * [GET] /users/history-searches/:userIdx
     * @return BaseResponse<GetUserSearchRes>
     */
    @ResponseBody
    @GetMapping("/history-searches/{userIdx}")
    public BaseResponse<List<GetUserSearchRes>> getUserSearches(@PathVariable("userIdx") int userIdx) {
        if (userIdx == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetUserSearchRes> getUserSearchRes = userProvider.getUserSearches(userIdx);
            return new BaseResponse<>(getUserSearchRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [11].
     * 특정 유저가 자신이 좋아요한 영상 조회 API
     * [GET] /users/:userIdx/video/liked
     * @return BaseResponse<GetVideosUserLikedRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/video/liked")
    public BaseResponse<List<GetVideosUserLikedRes>> getVideosUserLiked(@PathVariable("userIdx") int userIdx) {
        if (userIdx == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetVideosUserLikedRes> getVideosUserLikedRes = userProvider.getVideosUserLiked(userIdx);
            return new BaseResponse<>(getVideosUserLikedRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * [12].
     * 특정 유저가 채널을 구독하는 기록 생성 API
     * [POST] /users/:userIdx/subs
     * @return BaseResponse<PostSubsrbiesRes>
     */
    @ResponseBody
    @PostMapping("/{userIdx}/subs")
    public BaseResponse<PostSubscribeRes> postSubs(@PathVariable("userIdx") int userIdx, @RequestBody PostSubscribeReq postSubscribeReq) {
        if (userIdx == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            postSubscribeReq = new PostSubscribeReq(userIdx, postSubscribeReq.getSubscribeUserIdx());
            PostSubscribeRes postSubscribeRes = userService.postSubs(postSubscribeReq);
            return new BaseResponse<>(postSubscribeRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [13].
     * 특정 유저 검색어 기록 생성 API
     * [POST] /users/:userIdx/history-search
     * @return BaseResponse<PostSearchRes>
     */
    @ResponseBody
    @PostMapping("/{userIdx}/history-search")
    public BaseResponse<PostSearchRes> postSearch(@PathVariable("userIdx") int userIdx, @RequestBody PostSearchReq postSearchReq) {
        if (userIdx == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            postSearchReq = new PostSearchReq(userIdx, postSearchReq.getText());
            PostSearchRes postSearchRes = userService.postSearch(postSearchReq);
            return new BaseResponse<>(postSearchRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [14].
     * 특정 유저가 자신의 영상 기록 생성 API
     * [POST] /users/:userIdx/history-video
     * @return BaseResponse<PostVideoHistoryRes>
     */
    @ResponseBody
    @PostMapping("/{userIdx}/history-video")
    public BaseResponse<PostVideoHistoryRes> postVideoHistory(@PathVariable("userIdx") int userIdx, @RequestBody PostVideoHistoryReq postVideoHistoryReq) {
        if (userIdx == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        if (postVideoHistoryReq.getVideoIdx() == 0) {
            return new BaseResponse<>(EMPTY_VIDEO_IDX);
        }
        if (postVideoHistoryReq.getPlayTime() == null) {
            return new BaseResponse<>(EMPTY_VIDEO_PLAYTIME);
        }
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            /*
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
             */
            postVideoHistoryReq = new PostVideoHistoryReq(userIdx, postVideoHistoryReq.getVideoIdx(), postVideoHistoryReq.getPlayTime());
            PostVideoHistoryRes postVideoHistoryRes = userService.postVideoHistory(postVideoHistoryReq);
            return new BaseResponse<>(postVideoHistoryRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [15].
     * 특정 유저가 나중에 볼 동영상 추가 생성 API
     * [POST] /users/:userIdx/later-see/video
     * @return BaseResponse<PostLaterVideoRes>
     */
    @ResponseBody
    @PostMapping("/{userIdx}/later-see/video")
    public BaseResponse<PostLaterVideoRes> postLaterVideo(@PathVariable("userIdx") int userIdx, @RequestBody PostLaterVideoReq postLaterVideoReq) {
        if (userIdx == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        if (postLaterVideoReq.getVideoIdx() == 0) {
            return new BaseResponse<>(EMPTY_VIDEO_IDX);
        }
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            postLaterVideoReq = new PostLaterVideoReq(userIdx, postLaterVideoReq.getVideoIdx());
            PostLaterVideoRes postLaterVideoRes = userService.postLaterVideo(postLaterVideoReq);
            return new BaseResponse<>(postLaterVideoRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
