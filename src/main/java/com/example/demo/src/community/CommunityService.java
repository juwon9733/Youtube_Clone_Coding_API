package com.example.demo.src.community;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.comment.model.*;
import com.example.demo.src.community.model.*;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.UserProvider;
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
public class CommunityService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CommunityDao communityDao;
    private final CommunityProvider communityProvider;
    private final JwtService jwtService;

    public CommunityService(CommunityDao communityDao, CommunityProvider communityProvider, JwtService jwtService) {
        this.communityDao = communityDao;
        this.communityProvider = communityProvider;
        this.jwtService = jwtService;
    }

    public PostCommContentRes postCommContent(PostCommContentReq postCommContentReq) throws BaseException {
        if(communityProvider.checkUserIdx(postCommContentReq.getUserIdx()) != 1) {
            throw new BaseException(USER_IDX_NOT_EXISTS);
        }
        try{
            int commIdx = communityDao.postCommContent(postCommContentReq);
            return new PostCommContentRes(commIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public PostCommPictureRes postCommPicture(PostCommPictureReq postCommPictureReq) throws BaseException {
        if(communityProvider.checkCommIdx(postCommPictureReq.getCommIdx()) != 1) {
            throw new BaseException(COMMUNITY_IDX_NOT_EXISTS);
        }
        try{
            int imageUrlIdx = communityDao.postCommPicture(postCommPictureReq);
            return new PostCommPictureRes(imageUrlIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public void modifyCommunity(PatchCommunityReq patchCommunityReq) throws BaseException {
        if(communityProvider.checkCommIdx(patchCommunityReq.getCommIdx()) != 1) {
            throw new BaseException(COMMUNITY_IDX_NOT_EXISTS);
        }
        if(communityProvider.checkCommIdxByUserIdx(patchCommunityReq) != 1) {
            throw new BaseException(COMMUNITY_IDX_NOT_USERS_CONTENT);
        }
        try{
            int result = communityDao.modifyCommunity(patchCommunityReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_COMMUNITY);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public void modifyCommunityStatus(PatchCommunityStatusReq patchCommunityStatusReq) throws BaseException {
        if(communityProvider.checkCommIdx(patchCommunityStatusReq.getIdx()) != 1) {
            throw new BaseException(COMMENT_IDX_NOT_EXISTS);
        }
        try{
            int result = communityDao.modifyCommunityStatus(patchCommunityStatusReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_COMMUNITY_STATUS);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public PostCommunityLikedRes postCommunityLike(PostCommunityLikedReq PostCommunityLikedReq) throws BaseException {
        try{
            int communityLikedIdx = communityDao.postCommunityLike(PostCommunityLikedReq);
            return new PostCommunityLikedRes(communityLikedIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public PostCommunityUnLikedRes postCommunityUnLike(PostCommunityUnLikedReq postCommunityUnLikedReq) throws BaseException {
        try{
            int communityUnLikedIdx = communityDao.postCommunityUnLike(postCommunityUnLikedReq);
            return new PostCommunityUnLikedRes(communityUnLikedIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
