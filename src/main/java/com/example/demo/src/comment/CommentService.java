package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.comment.model.*;

import com.example.demo.src.video.model.PatchVideoStatusReq;
import com.example.demo.src.video.model.PostVideoLikedReq;
import com.example.demo.src.video.model.PostVideoLikedRes;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
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
public class CommentService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CommentDao commentDao;
    private final CommentProvider commentProvider;
    private final JwtService jwtService;

    public CommentService(CommentDao commentDao, CommentProvider commentProvider, JwtService jwtService) {
        this.commentDao = commentDao;
        this.commentProvider = commentProvider;
        this.jwtService = jwtService;
    }

    public PostCommentRes createComment(PostCommentReq postCommentReq) throws BaseException {
        if(postCommentReq.getVideoIdx() !=0 &&commentProvider.checkVideoIdx(postCommentReq.getVideoIdx()) != 1) {
            throw new BaseException(VIDEO_IDX_NOT_EXISTS);
        }
        if(postCommentReq.getPostIdx() != 0&&commentProvider.checkPostIdx(postCommentReq.getPostIdx()) != 1) {
            throw new BaseException(POST_IDX_NOT_EXISTS);
        }
        try{
            int commentIdx = commentDao.createComment(postCommentReq);
            return new PostCommentRes(commentIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public void modifyComment(PatchCommentReq patchCommentReq) throws BaseException {
        if(commentProvider.checkCommentIdx(patchCommentReq.getIdx()) != 1) {
            throw new BaseException(COMMENT_IDX_NOT_EXISTS);
        }
        if(commentProvider.checkCommnetIdxByUserIdx(patchCommentReq) != 1) {
            throw new BaseException(COMMENT_IDX_NOT_USERS);
        }
        try{
            int result = commentDao.modifyComment(patchCommentReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_COMMENT);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public void modifyCommentStatus(PatchCommentStatusReq patchCommentStatusReq) throws BaseException {
        if(commentProvider.checkCommentIdx(patchCommentStatusReq.getIdx()) != 1) {
            throw new BaseException(COMMENT_IDX_NOT_EXISTS);
        }
        try{
            int result = commentDao.modifyCommentStatus(patchCommentStatusReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_COMMENT_STATUS);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public PostCommentLikedRes postCommentLike(PostCommentLikedReq postCommentLikedReq) throws BaseException {
        try{
            int commentLikedIdx = commentDao.postCommentLike(postCommentLikedReq);
            return new PostCommentLikedRes(commentLikedIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public PostCommentUnLikedRes postCommentUnLike(PostCommentUnLikedReq postCommentUnLikedReq) throws BaseException {
        try{
            int commentUnLikedIdx = commentDao.postCommentUnLike(postCommentUnLikedReq);
            return new PostCommentUnLikedRes(commentUnLikedIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
