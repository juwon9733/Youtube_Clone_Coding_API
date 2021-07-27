package com.example.demo.src.reply;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.reply.model.*;
import com.example.demo.src.video.model.PostVideoLikedReq;
import com.example.demo.src.video.model.PostVideoLikedRes;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Transactional
@Service
public class ReplyService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ReplyDao replyDao;
    private final ReplyProvider replyProvider;
    private final JwtService jwtService;

    @Autowired
    public ReplyService(ReplyDao replyDao, ReplyProvider replyProvider, JwtService jwtService) {
        this.replyDao = replyDao;
        this.replyProvider = replyProvider;
        this.jwtService = jwtService;
    }
    public PostReplyRes createReply(PostReplyReq postReplyReq) throws BaseException {
        if(replyProvider.checkCommentIdx(postReplyReq.getCommentIdx()) != 1) {
            throw new BaseException(COMMENT_IDX_NOT_EXISTS);
        }
        try{
            int replyIdx = replyDao.createReply(postReplyReq);
            return new PostReplyRes(replyIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public void modifyReply(PatchReplyReq patchReplyReq) throws BaseException {
        if(replyProvider.checkReplyIdx(patchReplyReq.getReplyIdx()) != 1) {
            throw new BaseException(REPLY_IDX_NOT_EXISTS);
        }
        if(replyProvider.checkReplyIdxByUserIdx(patchReplyReq) != 1) {
            throw new BaseException(REPLY_IDX_NOT_USERS);
        }
        try{
            int result = replyDao.modifyReply(patchReplyReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_REPLY);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public void modifyReplyStatus(PatchReplyStatusReq patchReplyStatusReq) throws BaseException {
        if(replyProvider.checkReplyIdx(patchReplyStatusReq.getReplyIdx()) != 1) {
            throw new BaseException(REPLY_IDX_NOT_EXISTS);
        }
        try{
            int result = replyDao.modifyReplyStatus(patchReplyStatusReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_REPLY_STATUS);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public PostReplyLikedRes postReplyLike(PostReplyLikedReq postReplyLikedReq) throws BaseException {
        try{
            int replyLikedIdx = replyDao.postReplyLike(postReplyLikedReq);
            return new PostReplyLikedRes(replyLikedIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public PostReplyUnLikedRes postReplyUnLike(PostReplyUnLikedReq postReplyUnLikedReq) throws BaseException {
        try{
            int replyUnLikedIdx = replyDao.postReplyUnLike(postReplyUnLikedReq);
            return new PostReplyUnLikedRes(replyUnLikedIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
