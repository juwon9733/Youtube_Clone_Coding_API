package com.example.demo.src.reply;

import com.example.demo.src.reply.ReplyProvider;
import com.example.demo.src.reply.ReplyService;
import com.example.demo.src.reply.model.*;
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
@RequestMapping("/app/replys")
public class ReplyController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final ReplyProvider replyProvider;
    @Autowired
    private final ReplyService replyService;
    @Autowired
    private final JwtService jwtService;

    public ReplyController(ReplyProvider replyProvider, ReplyService replyService, JwtService jwtService) {
        this.replyProvider = replyProvider;
        this.replyService = replyService;
        this.jwtService = jwtService;
    }
    /**
     * [41].
     * 특정 코멘트의 답글 조회 API
     * [GET] /replys/:commentIdx
     * @return BaseResponse<List<GetReplyRes>>
     */
    @ResponseBody
    @GetMapping("/{commentIdx}")
    public BaseResponse<List<GetReplyRes>> getReplys(@PathVariable("commentIdx") int commentIdx) {
        try{
            List<GetReplyRes> getReplyRes = replyProvider.getReplys(commentIdx);
            return new BaseResponse<>(getReplyRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [42].
     * 특정 유저가 특정 코멘트에 답글 생성 API
     * [POST] /replys
     * @return BaseResponse<PostReplyRes>
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostReplyRes> createReply(@RequestBody PostReplyReq postReplyReq) {
        if(postReplyReq.getUserIdx() == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        if(postReplyReq.getCommentIdx() == 0) {
            return new BaseResponse<>(EMPTY_COMMENT_IDX);
        }
        if(postReplyReq.getReply() == null) {
            return new BaseResponse<>(EMPTY_REPLY);
        }
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(postReplyReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostReplyRes postReplyRes = replyService.createReply(postReplyReq);
            return new BaseResponse<>(postReplyRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [43].
     * 특정 유저가 자신의 답글 수정 API
     * [PATCH] /replys/:replyIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{replyIdx}")
    public BaseResponse<String> modifyReply(@PathVariable("replyIdx") int replyIdx, @RequestBody Reply reply) {
        Integer temp_userIdx = reply.getUserIdx();
        if(temp_userIdx == null) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        if(reply.getReply() == null) {
            return new BaseResponse<>(EMPTY_REPLY);
        }
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(reply.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PatchReplyReq patchReplyReq = new PatchReplyReq(replyIdx, reply.getUserIdx(),reply.getReply());
            replyService.modifyReply(patchReplyReq);
            String result = "Reply modified";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [44].
     * 특정 유저가 자신의 답글 상태변경(활성화, 비활성화) API
     * [PATCH] /replys/status/:replyIdx
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/status/{replyIdx}")
    public BaseResponse<String> modifyReplyStatus(@PathVariable("replyIdx") int replyIdx, @RequestBody PatchReplyStatusReq patchReplyStatusReq) {
        Integer temp_userIdx = patchReplyStatusReq.getUserIdx();
        if(temp_userIdx == null) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }

        if(patchReplyStatusReq.getStatus() == null) {
            return new BaseResponse<>(EMPTY_REPLY_STATUS);
        }
        else if(patchReplyStatusReq.getStatus() != "Y" || patchReplyStatusReq.getStatus() != "N") {
            return new BaseResponse<>(INVALID_REPLY_STATUS);
        }

        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(patchReplyStatusReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            patchReplyStatusReq = new PatchReplyStatusReq(replyIdx, patchReplyStatusReq.getUserIdx(), patchReplyStatusReq.getStatus());
            replyService.modifyReplyStatus(patchReplyStatusReq);
            String result = "Reply Status modified";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [45].
     * 특정 답글의 좋아요수 조회 API
     * [GET] /replys/:replyIdx/liked
     * @return BaseResponse<List<GetVideoLikedRes>>
     */
    @ResponseBody
    @GetMapping("/{replyIdx}/liked")
    public BaseResponse<GetReplyLikedRes> getReplyLiked(@PathVariable("replyIdx") int replyIdx) {
        try {
            GetReplyLikedRes getReplyLikedRes = replyProvider.getReplyLiked(replyIdx);
            return new BaseResponse<>(getReplyLikedRes);
        }catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [46].
     * 특정 답글의 싫어요수 조회 API
     * [GET] /replys/:replyIdx/unliked
     * @return BaseResponse<List<GetReplyUnLikedRes>>
     */
    @ResponseBody
    @GetMapping("/{replyIdx}/unliked")
    public BaseResponse<GetReplyUnLikedRes> getReplyUnLiked(@PathVariable("replyIdx") int replyIdx) {
        try {
            GetReplyUnLikedRes getReplyUnLikedRes = replyProvider.getReplyUnLiked(replyIdx);
            return new BaseResponse<>(getReplyUnLikedRes);
        }catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * [47].
     * 특정 유저가 특정 답글 좋아요 생성 API
     * [POST] /replys/liked
     * @return BaseResponse<PostReplyLikedRes>
     */
    @ResponseBody
    @PostMapping("/liked")
    public BaseResponse<PostReplyLikedRes> postReplyLike(@RequestBody PostReplyLikedReq postReplyLikedReq) {
        Integer temp_userIdx, temp_replyIdx;
        temp_userIdx= postReplyLikedReq.getUserIdx();
        temp_replyIdx = postReplyLikedReq.getReplyIdx();

        if(temp_userIdx == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        if(temp_replyIdx == 0) {
            return new BaseResponse<>(EMPTY_REPLY_IDX);
        }
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(postReplyLikedReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostReplyLikedRes postReplyLikedRes = replyService.postReplyLike(postReplyLikedReq);
            return new BaseResponse<>(postReplyLikedRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
    /**
     * [48].
     * 특정 유저가 특정 답글 싫어요 생성 API
     * [POST] /replys/unliked
     * @return BaseResponse<PostReplyUnLikedRes>
     */
    @ResponseBody
    @PostMapping("/unliked")
    public BaseResponse<PostReplyUnLikedRes> postReplyUnLike(@RequestBody PostReplyUnLikedReq postReplyUnLikedReq) {
        Integer temp_userIdx, temp_replyIdx;
        temp_userIdx= postReplyUnLikedReq.getUserIdx();
        temp_replyIdx = postReplyUnLikedReq.getReplyIdx();

        if(temp_userIdx == 0) {
            return new BaseResponse<>(EMPTY_USER_IDX);
        }
        if(temp_replyIdx == 0) {
            return new BaseResponse<>(EMPTY_REPLY_IDX);
        }
        try{
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(postReplyUnLikedReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PostReplyUnLikedRes postReplyUnLikedRes = replyService.postReplyUnLike(postReplyUnLikedReq);
            return new BaseResponse<>(postReplyUnLikedRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
