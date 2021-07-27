package com.example.demo.src.comment;

import com.example.demo.src.comment.CommentProvider;
import com.example.demo.src.comment.CommentService;
import com.example.demo.src.comment.model.*;
import com.example.demo.src.video.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;
@RestController
@RequestMapping("/app/comments")
public class CommentController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final CommentProvider commentProvider;
    @Autowired
    private final CommentService commentService;
    @Autowired
    private final JwtService jwtService;

    public CommentController(CommentProvider commentProvider, CommentService commentService, JwtService jwtService) {
        this.commentProvider = commentProvider;
        this.commentService = commentService;
        this.jwtService = jwtService;
    }
    /**
     * [31].
     * 전체 코멘트 조회 API
     * [GET] /comments
     * @return BaseResponse<List<GetCommentRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetCommentRes>> getComments() {
        try{
            List<GetCommentRes> getCommentRes = commentProvider.getComments();
            return new BaseResponse<>(getCommentRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [32].
     * 특정 유저가 자신의 코멘트 생성 API
     * [POST] /comments
     * @return BaseResponse<PostCommentRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostCommentRes> createComment(@RequestBody PostCommentReq postCommentReq) {
        if(postCommentReq.getVideoIdx() != 0 && postCommentReq.getPostIdx() != 0) {
            return new BaseResponse<>(COMMENT_IDX_DUPLICATED);
        }
        if(postCommentReq.getUserIdx() == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        if(postCommentReq.getComment() == null) {
            return new BaseResponse<>(EMPTY_COMMENT);
        }
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(postCommentReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostCommentRes postCommentRes = commentService.createComment(postCommentReq);
            return new BaseResponse<>(postCommentRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [33].
     * 특정 비디오 코멘트 조회 API
     * [GET] /comments/video:videoIdx
     * @return BaseResponse<List<GetCommentRes>>
     */
    @ResponseBody
    @GetMapping("/video/{videoIdx}")
    public BaseResponse<List<GetCommentRes>> getCommentsVideo(@PathVariable("videoIdx") int videoIdx) {
        try{
            List<GetCommentRes> getCommentRes = commentProvider.getCommentsVideo(videoIdx);
            return new BaseResponse<>(getCommentRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [34].
     * 특정 게시물 코멘트 조회 API
     * [GET] /comments/post:postIdx
     * @return BaseResponse<List<GetCommentRes>>
     */
    @ResponseBody
    @GetMapping("/post/{postIdx}")
    public BaseResponse<List<GetCommentRes>> getCommentsPost(@PathVariable("postIdx") int postIdx) {
        try {
            List<GetCommentRes> getCommentRes = commentProvider.getCommentsPost(postIdx);
            return new BaseResponse<>(getCommentRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [35].
     * 특정 유저가 자신의 코멘트 수정 API
     * [PATCH] /comments/:commentIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{commentIdx}")
    public BaseResponse<String> modifyComment(@PathVariable("commentIdx") int commentIdx, @RequestBody PatchCommentReq patchCommentReq) {
        Integer temp_userIdx = patchCommentReq.getUserIdx();
        if(temp_userIdx == null) {
            return new BaseResponse<>(USER_IDX_NOT_EXISTS);
        }
        if(patchCommentReq.getComment() == null) {
            return new BaseResponse<>(EMPTY_COMMENT);
        }
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(patchCommentReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            patchCommentReq = new PatchCommentReq(commentIdx, patchCommentReq.getIdx(), patchCommentReq.getComment());
            commentService.modifyComment(patchCommentReq);
            String result = "comment modified";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [36].
     * 특정 유저가 자신의 코멘트 상태변경(활성화, 비활성화) API
     * [PATCH] /comments/status/:commentIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/status/{commentIdx}")
    public BaseResponse<String> modifyCommentStatus(@PathVariable("commentIdx") int commentIdx, @RequestBody PatchCommentStatusReq patchCommentStatusReq) {
        if(patchCommentStatusReq.getStatus() == null) {
            return new BaseResponse<>(EMPTY_COMMENT_STATUS);
        }
        else if(patchCommentStatusReq.getStatus() != "Y" || patchCommentStatusReq.getStatus() != "N") {
            return new BaseResponse<>(INVALID_COMMENT_STATUS);
        }
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(patchCommentStatusReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            patchCommentStatusReq = new PatchCommentStatusReq(commentIdx, patchCommentStatusReq.getUserIdx(),
                    patchCommentStatusReq.getStatus());
            commentService.modifyCommentStatus(patchCommentStatusReq);
            String result = "Comment Status modified";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [37].
     * 특정 코멘트의 좋아요수 조회 API
     * [GET] /videos/:commentIdx/liked
     * @return BaseResponse<List<GetCommentLikedRes>>
     */
    @ResponseBody
    @GetMapping("/{commentIdx}/liked")
    public BaseResponse<GetCommentLikedRes> getCommentLiked(@PathVariable("commentIdx") int commentIdx) {
        try {
            GetCommentLikedRes getCommentLikedRes = commentProvider.getCommentLiked(commentIdx);
            return new BaseResponse<>(getCommentLikedRes);
        }catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [38].
     * 특정 코멘트의 싫어요수 조회 API
     * [GET] /videos/:commentIdx/unliked
     * @return BaseResponse<List<GetCommentUnLikedRes>>
     */
    @ResponseBody
    @GetMapping("/{commentIdx}/unliked")
    public BaseResponse<GetCommentUnLikedRes> getCommentUnLiked(@PathVariable("commentIdx") int commentIdx) {
        try {
            GetCommentUnLikedRes getCommentUnLikedRes = commentProvider.getCommentUnLiked(commentIdx);
            return new BaseResponse<>(getCommentUnLikedRes);
        }catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * [39].
     * 특정 유저가 특정 코멘트 좋아요 생성 API
     * [POST] /comments/liked
     * @return BaseResponse<PostCommentLikedRes>
     */
    @ResponseBody
    @PostMapping("/liked")
    public BaseResponse<PostCommentLikedRes> postCommentLike(@RequestBody PostCommentLikedReq postCommentLikedReq) {
        Integer temp_userIdx, temp_commentIdx;
        temp_userIdx= postCommentLikedReq.getUserIdx();
        temp_commentIdx = postCommentLikedReq.getCommentIdx();

        if(temp_userIdx == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        if(temp_commentIdx == 0) {
            return new BaseResponse<>(EMPTY_COMMENT_IDX);
        }
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(postCommentLikedReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostCommentLikedRes postCommentLikedRes = commentService.postCommentLike(postCommentLikedReq);
            return new BaseResponse<>(postCommentLikedRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [40].
     * 특정 유저가 특정 코멘트 싫어요 생성 API
     * [POST] /comments/unliked
     * @return BaseResponse<PostCommentLikedRes>
     */
    @ResponseBody
    @PostMapping("/unliked")
    public BaseResponse<PostCommentUnLikedRes> postCommentUnLike(@RequestBody PostCommentUnLikedReq postCommentUnLikedReq) {
        Integer temp_userIdx, temp_commentIdx;
        temp_userIdx= postCommentUnLikedReq.getUserIdx();
        temp_commentIdx = postCommentUnLikedReq.getCommentIdx();

        if(temp_userIdx == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        if(temp_commentIdx == 0) {
            return new BaseResponse<>(EMPTY_COMMENT_IDX);
        }
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(postCommentUnLikedReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostCommentUnLikedRes postCommentUnLikedRes = commentService.postCommentUnLike(postCommentUnLikedReq);
            return new BaseResponse<>(postCommentUnLikedRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [41].
     * 특정 코멘트의 답글 수 조회 API
     * [GET] /comments/count-reply/:commentIdx
     * @return BaseResponse<GetCommentCountReplyRes>
     */
    @ResponseBody
    @GetMapping("/count-reply/{commentIdx}")
    public BaseResponse<GetCommentCountReplyRes> getCommentCountReply(@PathVariable("commentIdx") int commentIdx) {
        try {
            GetCommentCountReplyRes getCommentCountReplyRes = commentProvider.getCommentCountReply(commentIdx);
            return new BaseResponse<>(getCommentCountReplyRes);
        }catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
