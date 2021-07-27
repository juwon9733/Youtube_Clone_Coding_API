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

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    public List<GetUserRes> getUsers() throws BaseException{
        try{
            List<GetUserRes> getUserRes = userDao.getUsers();
            return getUserRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetUserRes> getUsersByEmail(String email) throws BaseException{
        if(checkEmail(email) != 1) {
            throw new BaseException(USER_EMAIL_NOT_EXISTS);
        }
        try{
            List<GetUserRes> getUsersRes = userDao.getUsersByEmail(email);
            return getUsersRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public GetUserRes getUserByUserIdx(int userIdx) throws BaseException {
        if(checkUserIdx(userIdx) != 1) {
            throw new BaseException(USER_IDX_NOT_EXISTS);
        }
        try {
            GetUserRes getUserRes = userDao.getUserByUserIdx(userIdx);
            return getUserRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException{
        if(checkEmail(postLoginReq.getEmail()) != 1){
            throw new BaseException(USER_EMAIL_NOT_EXISTS);
        }
        // Request의 Email로 user를 특정하여, user 정보를 가져온다.
        User user = userDao.getUserToLogInByEmail(postLoginReq);
        String password;
        try {
            // 패스워드 복호화
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPasswd());
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }
        // Email로 가져온 user의 비밀번호를,
        // Request의 비밀번호와 비교한다.
        if(postLoginReq.getPasswd().equals(password)){
            int userIdx = userDao.getUserToLogInByEmail(postLoginReq).getIdx();
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx,jwt);
        }
        else{
            throw new BaseException(FAILED_TO_LOGIN_BY_PASSWD);
        }
    }
    public User getUserByIdx(int userIdx) throws BaseException{
        if(checkUserIdx(userIdx) != 1) {
            throw new BaseException(USER_IDX_NOT_EXISTS);
        }
        try{
            User user = userDao.getUserByIdx(userIdx);
            return user;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetUserRes> getUserSubs(int userIdx) throws BaseException {
        if(checkUserIdx(userIdx) != 1) {
            throw new BaseException(USER_IDX_NOT_EXISTS);
        }
        try {
            List<GetUserRes> getUserRes = userDao.getUserSubs(userIdx);
            return getUserRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetUserSearchRes> getUserSearches(int userIdx) throws BaseException{
        if(checkUserIdxInSearch(userIdx) != 1) {
            throw new BaseException(USER_IDX_NOT_EXISTS_IN_SEARCH);
        }
        try {
            List<GetUserSearchRes> getUserSearchRes = userDao.getUserSearches(userIdx);
            return getUserSearchRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetVideosUserLikedRes> getVideosUserLiked(int userIdx) throws BaseException{
        if(checkUserIdx(userIdx) != 1) {
            throw new BaseException(USER_IDX_NOT_EXISTS);
        }
        try {
            List<GetVideosUserLikedRes> getVideosUserLikedRes = userDao.getVideosUserLiked(userIdx);
            return getVideosUserLikedRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * check 관련 함수 모음
     */
    public int checkEmail(String email) throws BaseException{
        try{
            return userDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkUserIdx(int userIdx) throws BaseException {
        try{
            return userDao.checkUserIdx(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkUserIdxInSearch(int userIdx) throws BaseException {
        try{
            return userDao.checkUserIdxInSearch(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkSubsByUserIdx(PostSubscribeReq postSubscribeReq) throws BaseException {
        try{
            return userDao.checkSubsByUserIdx(postSubscribeReq);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkVideoIdx(int videoIdx) throws BaseException {
        try{
            return userDao.checkVideoIdx(videoIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public boolean checkDeltedToken(String JwtToken) throws BaseException {
        try{
            return userDao.checkDeltedToken(JwtToken);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
