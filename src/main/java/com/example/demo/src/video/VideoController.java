package com.example.demo.src.video;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.video.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/app/videos")
public class VideoController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final VideoProvider videoProvider;
    @Autowired
    private final VideoService videoService;
    @Autowired
    private final JwtService jwtService;

    public VideoController(VideoProvider videoProvider, VideoService videoService, JwtService jwtService){
        this.videoProvider = videoProvider;
        this.videoService = videoService;
        this.jwtService = jwtService;
    }
    /**
     * [16].
     * 전체 비디오 조회 API
     * [GET] /videos
     * 비디오 제목 검색 조회 API
     * [GET] /videos? Title=
     * @return BaseResponse<List<GetVideoRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetVideoRes>> getVidoes(@RequestParam(required = false) String Title) {
        try{
            if(Title == null){
                List<GetVideoRes> getVideoRes = videoProvider.getVideos();
                return new BaseResponse<>(getVideoRes);
            }
            List<GetVideoRes> getVideosRes = videoProvider.getVideosByTitle(Title);
            return new BaseResponse<>(getVideosRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [17].
     * 비디오 조회 API
     * [GET] /videos/:videoIdx
     * @return BaseResponse<GetVideoRes>
     */
    @ResponseBody
    @GetMapping("/{videoIdx}")
    public BaseResponse<GetVideoRes> getVideo(@PathVariable("videoIdx") int videoIdx) {
        if(videoIdx == 0) {
            return new BaseResponse<>(EMPTY_VIDEO_IDX);
        }
        try {
            GetVideoRes getVideoRes = videoProvider.getVideo(videoIdx);
            return new BaseResponse<>(getVideoRes);
        }catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [18].
     * 특정 유저가 자신의 비디오 생성 API
     * [POST] /videos
     * @return BaseResponse<PostVideoRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostVideoRes> createVideo(@RequestBody PostVideoReq postVideoReq) {
        if(postVideoReq.getUserIdx() == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        if(postVideoReq.getVideoUrl() == null) {
            return new BaseResponse<>(EMPTY_VIDEO_URL);
        }
        if(postVideoReq.getTitle() == null) {
            return new BaseResponse<>(EMPTY_VIDEO_TITLE);
        }
        if(postVideoReq.getVideoTime() == null) {
            return new BaseResponse<>(EMPTY_VIDEO_PLAYTIME);
        }
        if(postVideoReq.getKind() == null) {
            return new BaseResponse<>(EMPTY_VIDEO_KIND);
        }
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(postVideoReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostVideoRes postVideoRes = videoService.createVideo(postVideoReq);
            return new BaseResponse<>(postVideoRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [19].
     * 특정 유저가 자신의 비디오 정보 수정 API
     * [PATCH] /videos/:videoIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{videoIdx}")
    public BaseResponse<String> modifyVideo(@PathVariable("videoIdx") int videoIdx, @RequestBody PatchModifyVideoReq patchModifyVideoReq) {
        if(patchModifyVideoReq.getUserIdx() == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        if(patchModifyVideoReq.getVideoUrl() == null) {
            return new BaseResponse<>(EMPTY_VIDEO_URL);
        }
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(patchModifyVideoReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PatchVideoReq patchVideoReq = new PatchVideoReq(videoIdx, patchModifyVideoReq.getUserIdx(),
                    patchModifyVideoReq.getVideoUrl(), patchModifyVideoReq.getTitle(),
                    patchModifyVideoReq.getVideoExplain(), patchModifyVideoReq.getVideoTime(),
                    patchModifyVideoReq.getKind());
            videoService.modifyVideo(patchVideoReq);
            String result = "Video Modified";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [20].
     * 특정 유저가 자신의 비디오 상태변경(활성화, 비활성화) API
     * [PATCH] /videos/status/:videoIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/status/{videoIdx}")
    public BaseResponse<String> modifyVideoStatus(@PathVariable("videoIdx") int videoIdx, @RequestBody PatchVideoStatusReq patchVideoStatusReq) {
        if(patchVideoStatusReq.getStatus() == null) {
            return new BaseResponse<>(EMPTY_VIDEO_STATUS);
        }
        else if(patchVideoStatusReq.getStatus().equals("Y") == false && patchVideoStatusReq.getStatus().equals("N") == false){
            return new BaseResponse<>(INVALID_VIDEO_STATUS);
        }
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(patchVideoStatusReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            patchVideoStatusReq = new PatchVideoStatusReq(videoIdx, patchVideoStatusReq.getUserIdx(),patchVideoStatusReq.getStatus());
            videoService.modifyVideoStatus(patchVideoStatusReq);
            String result = "Video Status modified";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [21].
     * 특정 유저 채널의 영상 조회 API
     * [GET] /videos/user-channel/:userIdx
     * @return BaseResponse<List<GetVideoRes>>
     */
    @ResponseBody
    @GetMapping("/user-channel/{userIdx}")
    public BaseResponse<List<GetVideoRes>> getVideosByUser(@PathVariable("userIdx") int userIdx) {
        if(userIdx == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        try {
            List<GetVideoRes> getVideoRes = videoProvider.getVideosByUser(userIdx);
            return new BaseResponse<>(getVideoRes);
        }catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * [22].
     * 특정 사용자가 자신의 영상 기록 조회 API
     * [GET] /videos/history/:userIdx
     * @return BaseResponse<List<GetVideoRes>>
     */
    @ResponseBody
    @GetMapping("/history/{userIdx}")
    public BaseResponse<List<GetVideoUserHistoryRes>> getVideosUserHistory(@PathVariable("userIdx") int userIdx) {
        if(userIdx == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetVideoUserHistoryRes> getVideoUserHistoryRes = videoProvider.getVideosUserHistory(userIdx);
            return new BaseResponse<>(getVideoUserHistoryRes);
        }catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [23].
     * 특정 사용자가 자신의 나중에 볼 영상 조회 API
     * [GET] /videos/later-see/:userIdx
     * @return BaseResponse<List<GetVideoRes>>
     */
    @ResponseBody
    @GetMapping("/later-see/{userIdx}")
    public BaseResponse<List<GetVideoRes>> getVideosLaterSee(@PathVariable("userIdx") int userIdx) {
        if(userIdx == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetVideoRes> getVideoRes = videoProvider.getVideosLaterSee(userIdx);
            return new BaseResponse<>(getVideoRes);
        }catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [24].
     * 특정 비디오의 좋아요수 조회 API
     * [GET] /videos/:videoIdx/liked
     * @return BaseResponse<List<GetVideoLikedRes>>
     */
    @ResponseBody
    @GetMapping("/{videoIdx}/liked")
    public BaseResponse<GetVideoLikedRes> getVideoLiked(@PathVariable("videoIdx") int videoIdx) {
        if(videoIdx == 0) {
            return new BaseResponse<>(EMPTY_VIDEO_IDX);
        }
        try {
            GetVideoLikedRes getVideoLikedRes = videoProvider.getVideoLiked(videoIdx);
            return new BaseResponse<>(getVideoLikedRes);
        }catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [25].
     * 특정 비디오의 싫어요수 조회 API
     * [GET] /videos/:videoIdx/unliked
     * @return BaseResponse<List<GetVideoLikedRes>>
     */
    @ResponseBody
    @GetMapping("/{videoIdx}/unliked")
    public BaseResponse<GetVideoUnLikedRes> getVideoUnLiked(@PathVariable("videoIdx") int videoIdx) {
        if(videoIdx == 0) {
            return new BaseResponse<>(EMPTY_VIDEO_IDX);
        }
        try {
            GetVideoUnLikedRes getVideoUnLikedRes = videoProvider.getVideoUnLiked(videoIdx);
            return new BaseResponse<>(getVideoUnLikedRes);
        }catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [26].
     * 특정 유저가 특정 비디오 좋아요 생성 API
     * [POST] /videos/liked
     * @return BaseResponse<PostVideoLikedRes>
     */
    @ResponseBody
    @PostMapping("/liked")
    public BaseResponse<PostVideoLikedRes> postVideoLike(@RequestBody PostVideoLikedReq postVideoLikedReq) {
        if(postVideoLikedReq.getUserIdx() == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        if(postVideoLikedReq.getVideoIdx() == 0) {
            return new BaseResponse<>(EMPTY_VIDEO_IDX);
        }

        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(postVideoLikedReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostVideoLikedRes postVideoLikedRes = videoService.postVideoLike(postVideoLikedReq);
            return new BaseResponse<>(postVideoLikedRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [27].
     * 특정 유저가 특정 비디오 싫어요 생성 API
     * [POST] /videos/liked
     * @return BaseResponse<PostVideoLikedRes>
     */
    @ResponseBody
    @PostMapping("/unliked")
    public BaseResponse<PostVideoUnLikedRes> postVideoUnLike(@RequestBody PostVideoUnLikedReq postVideoUnLikedReq) {
        if(postVideoUnLikedReq.getUserIdx() == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        if(postVideoUnLikedReq.getVideoIdx() == 0) {
            return new BaseResponse<>(EMPTY_VIDEO_IDX);
        }

        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(postVideoUnLikedReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostVideoUnLikedRes postVideoUnLikedRes = videoService.postVideoUnLike(postVideoUnLikedReq);
            return new BaseResponse<>(postVideoUnLikedRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [28].
     * 특정 비디오의 댓글수 조회 API
     * [GET] /videos/count-reply/:videoIdx
     * @return BaseResponse<GetVideoCountCommentRes>
     */
    @ResponseBody
    @GetMapping("/count-comment/{videoIdx}")
    public BaseResponse<GetVideoCountCommentRes> getVideoCountComment(@PathVariable("videoIdx") int videoIdx) {
        if(videoIdx == 0) {
            return new BaseResponse<>(EMPTY_VIDEO_IDX);
        }
        try {
            GetVideoCountCommentRes getVideoCountCommentRes = videoProvider.getVideoCountComment(videoIdx);
            return new BaseResponse<>(getVideoCountCommentRes);
        }catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [29].
     * 특정 유저의 플레이리스트 조회 API
     * [GET] /videos/playlist/:userIdx
     * @return BaseResponse<GetPlayListRes>
     */
    @ResponseBody
    @GetMapping("/playlist/{userIdx}")
    public BaseResponse<List<GetPlayListRes>> getPlayList(@PathVariable("userIdx") int userIdx) {
        if(userIdx == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        try {
            List<GetPlayListRes> getVideoPlayListRes = videoProvider.getPlayList(userIdx);
            return new BaseResponse<>(getVideoPlayListRes);
        }catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [30].
     * 특정 플레이리스트에 속하는 비디오 조회 API
     * [GET] /videos/playlist/:playListIdx
     * @return BaseResponse<GetVideoPlayListRes>
     */
    @ResponseBody
    @GetMapping("/playlist/in/{playListIdx}")
    public BaseResponse<List<GetVideoPlayListRes>> getVideoPlayList(@PathVariable("playListIdx") int playListIdx) {
        try {
            List<GetVideoPlayListRes> getVideoPlayListRes = videoProvider.getVideoPlayList(playListIdx);
            return new BaseResponse<>(getVideoPlayListRes);
        }catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
