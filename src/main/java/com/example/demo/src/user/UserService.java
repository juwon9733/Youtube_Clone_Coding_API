package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.*;
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

// Service Create, Update, Delete 의 로직 처리
@Service
@Transactional
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        // 중복 확인
        if(userProvider.checkEmail(postUserReq.getEmail()) == 1){
            throw new BaseException(DUPLICATED_EMAIL);
        }
        String pwd;
        try{
            // 비밀번호 암호화
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPasswd());
            // 암호화된 비밀번호로, Request의 비밀번호 변경
            postUserReq.setPasswd(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try{
            int userIdx = userDao.createUser(postUserReq);
            // 해당 userIdx에, jwt 발급
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(jwt,userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public PostLogoutRes logOut(String userJwtToken) throws BaseException {
        try {
            int deletedJwtIdx = userDao.logOut(userJwtToken);
            return new PostLogoutRes(deletedJwtIdx, userJwtToken);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }
    public void modifyUserInfo(PatchUserReq patchUserReq) throws BaseException {
        // null이라면 이전 값으로 repalce.
        if(patchUserReq.getImageUrl() == null) {
            User user = userProvider.getUserByIdx(patchUserReq.getUserIdx());
            patchUserReq.setImageUrl(user.getImageUrl());
        }
        if(patchUserReq.getName() == null) {
            User user = userProvider.getUserByIdx(patchUserReq.getUserIdx());
            patchUserReq.setName(user.getName());
        }
        if(patchUserReq.getBirth() == null) {
            User user = userProvider.getUserByIdx(patchUserReq.getUserIdx());
            patchUserReq.setBirth(user.getBirth());
        }
        if(patchUserReq.getSex() == null) {
            User user = userProvider.getUserByIdx(patchUserReq.getUserIdx());
            patchUserReq.setSex(user.getSex());
        }
        if(patchUserReq.getPhoneNumber() == null) {
            User user = userProvider.getUserByIdx(patchUserReq.getUserIdx());
            patchUserReq.setPhoneNumber(user.getPhoneNumber());
        }
        if(patchUserReq.getPasswd() == null) {      // 새로운 pwd값이 안들어온다면, 이전의 pwd값을 사용
            User user = userProvider.getUserByIdx(patchUserReq.getUserIdx());
            patchUserReq.setPasswd(user.getPasswd());
        }
        else {
            // 새로운 pwd값이 들어온다면, 새롭게 pwd 암호화
            String pwd;
            try{
                pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(patchUserReq.getPasswd());
                // 암호화된 비밀번호로, 비밀번호 변경
                patchUserReq.setPasswd(pwd);
            } catch (Exception ignored) {
                throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
            }
        }
        try{
            int result = userDao.modifyUserInfo(patchUserReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_USER);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public void modifyUserStatusInfo(PatchModifyUserStatusReq patchModifyUserStatusReq) throws BaseException {
        if(userProvider.checkUserIdx(patchModifyUserStatusReq.getUserIdx()) != 1) {
            throw new BaseException(USER_IDX_NOT_EXISTS);
        }
        try{
            int result = userDao.modifyUserStatusInfo(patchModifyUserStatusReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_USER_STATUS);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public PostSubscribeRes postSubs(PostSubscribeReq postSubscribeReq) throws BaseException{
        if(userProvider.checkUserIdx(postSubscribeReq.getUserIdx()) != 1) {
            throw new BaseException(USER_IDX_NOT_EXISTS);
        }
        if(userProvider.checkSubsByUserIdx(postSubscribeReq) == 1) {
            throw new BaseException(DUPLICATED_SUBS);
        }
        try {
            PostSubscribeRes postSubscribeRes = new PostSubscribeRes(userDao.postSubs(postSubscribeReq));
            return postSubscribeRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public PostSearchRes postSearch(PostSearchReq postSearchReq) throws BaseException{
        if(userProvider.checkUserIdx(postSearchReq.getUserIdx()) != 1) {
            throw new BaseException(USER_IDX_NOT_EXISTS);
        }
        try {
            PostSearchRes postSearchRes = new PostSearchRes(userDao.postSearch(postSearchReq));
            return postSearchRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public PostVideoHistoryRes postVideoHistory(PostVideoHistoryReq postVideoHistoryReq) throws BaseException{
        if(userProvider.checkUserIdx(postVideoHistoryReq.getUserIdx()) != 1) {
            throw new BaseException(USER_IDX_NOT_EXISTS);
        }
        if(userProvider.checkVideoIdx(postVideoHistoryReq.getVideoIdx()) != 1) {
            throw new BaseException(VIDEO_IDX_NOT_EXISTS);
        }
        try {
            PostVideoHistoryRes postVideoHistoryRes = new PostVideoHistoryRes(userDao.postVideoHistory(postVideoHistoryReq));
            return postVideoHistoryRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public PostLaterVideoRes postLaterVideo(PostLaterVideoReq postLaterVideoReq) throws BaseException{
        if(userProvider.checkUserIdx(postLaterVideoReq.getUserIdx()) != 1) {
            throw new BaseException(USER_IDX_NOT_EXISTS);
        }
        if(userProvider.checkVideoIdx(postLaterVideoReq.getVideoIdx()) != 1) {
            throw new BaseException(VIDEO_IDX_NOT_EXISTS);
        }
        try {
            PostLaterVideoRes postLaterVideoRes = new PostLaterVideoRes(userDao.postLaterVideo(postLaterVideoReq));
            return postLaterVideoRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
