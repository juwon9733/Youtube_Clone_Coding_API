package com.example.demo.src.community;

import com.example.demo.config.BaseException;
import com.example.demo.src.comment.model.GetCommentLikedRes;
import com.example.demo.src.community.model.*;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.src.video.model.GetVideoCountCommentRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class CommunityProvider {

    private final CommunityDao communityDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CommunityProvider(CommunityDao communityDao, JwtService jwtService) {
        this.communityDao = communityDao;
        this.jwtService = jwtService;
    }

    public List<GetCommunityRes> getCommunity(int userIdx) throws BaseException {
        if(checkUserIdx(userIdx) != 1) {
            throw new BaseException(USER_IDX_NOT_EXISTS);
        }
        try{
            List<GetCommunityRes> getCommunityRes = communityDao.getCommunity(userIdx);
            return getCommunityRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetCommPictureRes> getCommPicture(int commIdx) throws BaseException {
        if(checkCommIdx(commIdx) != 1) {
            throw new BaseException(COMM_IDX_NOT_EXISTS);
        }
        try{
            List<GetCommPictureRes> getCommPictureRes = communityDao.getCommPicture(commIdx);
            return getCommPictureRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public GetCommunityLikedRes getCommunityLiked(int commIdx) throws BaseException {
        if (checkCommIdx(commIdx) != 1) {
            throw new BaseException(COMM_IDX_NOT_EXISTS);
        }
        try{
            return communityDao.getCommunityLiked(commIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public GetCommunityUnLikedRes getCommunityUnLiked(int commIdx) throws BaseException {
        if (checkCommIdx(commIdx) != 1) {
            throw new BaseException(COMM_IDX_NOT_EXISTS);
        }
        try{
            return communityDao.getCommunityUnLiked(commIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public GetCommunityCountCommentRes getCommunityCountComment(int commIdx) throws BaseException {
        if (checkCommIdx(commIdx) != 1) {
            throw new BaseException(COMM_IDX_NOT_EXISTS);
        }
        try{
            return communityDao.getCommunityCountComment(commIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * check 관련 함수 모음
     */
    public int checkUserIdx(int userIdx) throws BaseException{
        try{
            return communityDao.checkUserIdx(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkCommIdx(int commIdx) throws BaseException{
        try{
            return communityDao.checkCommIdx(commIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkCommIdxByUserIdx(PatchCommunityReq patchCommunityReq) throws BaseException{
        try{
            return communityDao.checkCommIdxByUserIdx(patchCommunityReq);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
