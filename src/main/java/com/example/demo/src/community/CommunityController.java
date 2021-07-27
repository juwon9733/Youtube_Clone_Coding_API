package com.example.demo.src.community;

import com.example.demo.src.comment.model.*;
import com.example.demo.src.user.UserProvider;
import com.example.demo.src.user.UserService;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.src.video.model.GetVideoCountCommentRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.community.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@RestController
@RequestMapping("/app/communities")
public class CommunityController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final CommunityProvider communityProvider;
    @Autowired
    private final CommunityService communityService;
    @Autowired
    private final JwtService jwtService;

    public CommunityController(CommunityProvider communityProvider, CommunityService communityService, JwtService jwtService) {
        this.communityProvider = communityProvider;
        this.communityService = communityService;
        this.jwtService = jwtService;
    }
    /**
     * [49].
     * 특정 사용자의 커뮤니티 글 전체 조회 API
     * [GET] /communities/:userIdx
     * @return BaseResponse<List<GetCommunityRes>>
     */
    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<List<GetCommunityRes>> getCommunity(@PathVariable("userIdx") int userIdx) {
        try {
            List<GetCommunityRes> getCommunityRes = communityProvider.getCommunity(userIdx);
            return new BaseResponse<>(getCommunityRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [50].
     * 특정 게시글의 사진들 조회 API
     * [GET] /communities/picture/:commIdx
     * @return BaseResponse<List<GetCommPictureRes>>
     */
    @ResponseBody
    @GetMapping("/picture/{commIdx}")
    public BaseResponse<List<GetCommPictureRes>> getCommPicture(@PathVariable("commIdx") int commIdx) {
        try {
            List<GetCommPictureRes> getCommPictureRes = communityProvider.getCommPicture(commIdx);
            return new BaseResponse<>(getCommPictureRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [51].
     * 사용자가 자신의 채널의 커뮤니티에 글 생성 API
     * [GET] /communities/content/:commIdx
     * @return BaseResponse<PostCommContentRes>
     */
    @ResponseBody
    @PostMapping("/content")
    public BaseResponse<PostCommContentRes> postCommContent(@RequestBody PostCommContentReq postCommContentReq) {
        Integer temp_userIdx = postCommContentReq.getUserIdx();
        if(temp_userIdx == null) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        if(postCommContentReq.getContent() == null) {
            return new BaseResponse<>(EMPTY_COMMUNITY_CONTENT);
        }
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(postCommContentReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostCommContentRes postCommContentRes = communityService.postCommContent(postCommContentReq);
            return new BaseResponse<>(postCommContentRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [52].
     *  특정 게시글의 사진 생성 API
     * [GET] /communities/content/picture
     * @return BaseResponse<PostCommPictureRes>
     */
    @ResponseBody
    @PostMapping("/content/picture")
    public BaseResponse<PostCommPictureRes> postCommPicture(@RequestBody PostCommPictureReq postCommPictureReq) {
        Integer temp_commIdx = postCommPictureReq.getCommIdx();
        if(temp_commIdx == null) {
            return new BaseResponse<>(EMPTY_COMMUNITY_IDX);
        }
        if(postCommPictureReq.getImageUrl() == null) {
            return new BaseResponse<>(EMPTY_COMMUNITY_IMAGE);
        }
        try{
            PostCommPictureRes postCommPictureRes = communityService.postCommPicture(postCommPictureReq);
            return new BaseResponse<>(postCommPictureRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [53].
     * 사용자가 자신의 게시글 수정 API
     * [PATCH] /communities/:commIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{commIdx}")
    public BaseResponse<String> modifyCommunity(@PathVariable("commIdx") int commIdx, @RequestBody PatchCommunityReq patchCommunityReq) {
        Integer temp_userIdx = patchCommunityReq.getUserIdx();
        if(temp_userIdx == null) {
            return new BaseResponse<>(USER_IDX_NOT_EXISTS);
        }
        if(patchCommunityReq.getContent() == null) {
            return new BaseResponse<>(EMPTY_COMMUNITY_CONTENT);
        }
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(patchCommunityReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            patchCommunityReq = new PatchCommunityReq(commIdx, patchCommunityReq.getUserIdx(), patchCommunityReq.getContent());
            communityService.modifyCommunity(patchCommunityReq);
            String result = "Community modified";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * [54].
     * 사용자가 자신의 게시글 상태 변경(활성화, 비활성화) API
     * [PATCH] /communities/status/:commIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/status/{commIdx}")
    public BaseResponse<String> modifyCommunityStatus(@PathVariable("commIdx") int commIdx, @RequestBody PatchCommunityStatusReq patchCommunityStatusReq) {
        Integer temp_userIdx = patchCommunityStatusReq.getUserIdx();
        if(temp_userIdx == null) {
            return new BaseResponse<>(USER_IDX_NOT_EXISTS);
        }
        if(patchCommunityStatusReq.getStatus() == null) {
            return new BaseResponse<>(EMPTY_COMMENT_STATUS);
        }
        else if(patchCommunityStatusReq.getStatus().equals("Y") != true && patchCommunityStatusReq.getStatus().equals("N") != true) {
            return new BaseResponse<>(INVALID_COMMENT_STATUS);
        }
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(patchCommunityStatusReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            patchCommunityStatusReq = new PatchCommunityStatusReq(commIdx, patchCommunityStatusReq.getUserIdx(),
                    patchCommunityStatusReq.getStatus());
            communityService.modifyCommunityStatus(patchCommunityStatusReq);
            String result = "Community Status modified";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [55].
     * 특정 게시글 좋아요수 조회 API
     * [GET] /communities/:commIdx/liked
     * @return BaseResponse<List<GetCommunityLikedRes>>
     */
    @ResponseBody
    @GetMapping("/{commIdx}/liked")
    public BaseResponse<GetCommunityLikedRes> getCommunityLiked(@PathVariable("commIdx") int commIdx) {
        try {
            GetCommunityLikedRes getCommunityLikedRes = communityProvider.getCommunityLiked(commIdx);
            return new BaseResponse<>(getCommunityLikedRes);
        }catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [56].
     * 특정 게시글 싫어요수 조회 API
     * [GET] /communities/:commIdx/unliked
     * @return BaseResponse<List<GetCommunityUnLikedRes>>
     */
    @ResponseBody
    @GetMapping("/{commIdx}/unliked")
    public BaseResponse<GetCommunityUnLikedRes> getCommunityUnLiked(@PathVariable("commIdx") int commIdx) {
        try {
            GetCommunityUnLikedRes getCommunityUnLikedRes = communityProvider.getCommunityUnLiked(commIdx);
            return new BaseResponse<>(getCommunityUnLikedRes);
        }catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [57].
     * 특정 유저가 특정 게시글 좋아요 생성 API
     * [POST] /communities/liked
     * @return BaseResponse<PostCommunityLikedRes>
     */
    @ResponseBody
    @PostMapping("/liked")
    public BaseResponse<PostCommunityLikedRes> postCommunityLike(@RequestBody PostCommunityLikedReq postCommunityLikedReq) {
        Integer temp_userIdx, temp_commtIdx;
        temp_userIdx= postCommunityLikedReq.getUserIdx();
        temp_commtIdx = postCommunityLikedReq.getPostIdx();

        if(temp_userIdx == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        if(temp_commtIdx == 0) {
            return new BaseResponse<>(EMPTY_COMMUNITY_IDX);
        }
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(postCommunityLikedReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostCommunityLikedRes postCommunityLikedRes = communityService.postCommunityLike(postCommunityLikedReq);
            return new BaseResponse<>(postCommunityLikedRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [58].
     * 특정 유저가 특정 게시글 싫어요 생성 API
     * [POST] /communities/unliked
     * @return BaseResponse<PostCommunityUnLikedRes>
     */
    @ResponseBody
    @PostMapping("/unliked")
    public BaseResponse<PostCommunityUnLikedRes> postCommunityUnLike(@RequestBody PostCommunityUnLikedReq postCommunityUnLikedReq) {
        Integer temp_userIdx, temp_commtIdx;
        temp_userIdx= postCommunityUnLikedReq.getUserIdx();
        temp_commtIdx = postCommunityUnLikedReq.getPostIdx();

        if(temp_userIdx == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        if(temp_commtIdx == 0) {
            return new BaseResponse<>(EMPTY_COMMUNITY_IDX);
        }
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(postCommunityUnLikedReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostCommunityUnLikedRes postCommunityUnLikedRes = communityService.postCommunityUnLike(postCommunityUnLikedReq);
            return new BaseResponse<>(postCommunityUnLikedRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [59].
     * 특정 게시글의 댓글수 조회 API
     * [GET] /communities/count-comment/:commIdx
     * @return BaseResponse<GetCommunityCountCommentRes>
     */
    @ResponseBody
    @GetMapping("/count-comment/{commIdx}")
    public BaseResponse<GetCommunityCountCommentRes> getCommunityCountComment(@PathVariable("commIdx") int commIdx) {
        try {
            GetCommunityCountCommentRes getCommunityCountCommentRes = communityProvider.getCommunityCountComment(commIdx);
            return new BaseResponse<>(getCommunityCountCommentRes);
        }catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
