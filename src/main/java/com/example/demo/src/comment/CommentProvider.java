package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.comment.CommentDao;
import com.example.demo.src.comment.model.GetCommentRes;
import com.example.demo.src.comment.model.*;
import com.example.demo.src.video.model.GetVideoCountCommentRes;
import com.example.demo.src.video.model.GetVideoLikedRes;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
@Service
public class CommentProvider {
    private final CommentDao commentDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CommentProvider(CommentDao commentDao, JwtService jwtService) {
        this.commentDao = commentDao;
        this.jwtService = jwtService;
    }

    public List<GetCommentRes> getComments() throws BaseException{
        try{
            List<GetCommentRes> getCommentRes = commentDao.getComments();
            return getCommentRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetCommentRes> getCommentsVideo(int videoIdx) throws BaseException{
        if(checkVideoIdx(videoIdx) != 1) {
            throw new BaseException(VIDEO_IDX_NOT_EXISTS);
        }
        try{
            List<GetCommentRes> getCommentRes = commentDao.getCommentsVideo(videoIdx);
            return getCommentRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetCommentRes> getCommentsPost(int postIdx) throws BaseException{
        if(checkPostIdx(postIdx) != 1) {
            throw new BaseException(POST_IDX_NOT_EXISTS);
        }
        try{
            List<GetCommentRes> getCommentRes = commentDao.getCommentsPost(postIdx);
            return getCommentRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public GetCommentLikedRes getCommentLiked(int commentIdx) throws BaseException {
        if (checkCommentIdx(commentIdx) != 1) {
            throw new BaseException(COMMENT_IDX_NOT_EXISTS);
        }
        try{
            return commentDao.getCommentLiked(commentIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public GetCommentUnLikedRes getCommentUnLiked(int commentIdx) throws BaseException {
        if (checkCommentIdx(commentIdx) != 1) {
            throw new BaseException(COMMENT_IDX_NOT_EXISTS);
        }
        try{
            GetCommentUnLikedRes getCommentUnLikedRes = commentDao.getCommentUnLiked(commentIdx);

            return commentDao.getCommentUnLiked(commentIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public GetCommentCountReplyRes getCommentCountReply(int commentIdx) throws BaseException {
        if (checkCommentIdx(commentIdx) != 1) {
            throw new BaseException(COMMENT_IDX_NOT_EXISTS);
        }
        try{
            return commentDao.getCommentCountReply(commentIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * check 관련 함수 모음
     */
    public int checkVideoIdx(int videoIdx) throws BaseException {
        try{
            return commentDao.checkVideoIdx(videoIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkPostIdx(int postIdx) throws BaseException {
        try{
            return commentDao.checkPostIdx(postIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkCommentIdx(int commentIdx) throws BaseException {
        try{
            return commentDao.checkCommentIdx(commentIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkCommnetIdxByUserIdx(PatchCommentReq patchCommentReq) throws BaseException {
        try{
            return commentDao.checkCommnetIdxByUserIdx(patchCommentReq);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
