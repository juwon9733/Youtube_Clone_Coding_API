package com.example.demo.src.video;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.User;
import com.example.demo.src.video.VideoDao;
import com.example.demo.src.video.model.GetVideoRes;
import com.example.demo.src.video.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class VideoProvider {

    private final VideoDao videoDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    // @Autowired : 자동으로 객체를 생성해주는 어노테이션이다.
    public VideoProvider(VideoDao videoDao, JwtService jwtService) {
        this.videoDao = videoDao;
        this.jwtService = jwtService;
    }

    public List<GetVideoRes> getVideos() throws BaseException{
        try{
            List<GetVideoRes> getVideoRes = videoDao.getVideos();
            return getVideoRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetVideoRes> getVideosByTitle(String title) throws BaseException{
        try{
            List<GetVideoRes> getVideosRes = videoDao.getVideosByTitle(title);
            return getVideosRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public GetVideoRes getVideo(int videoIdx) throws BaseException{
        if (checkVideoIdx(videoIdx) != 1) {
            throw new BaseException(VIDEO_IDX_NOT_EXISTS);
        }
        try{
            GetVideoRes getVideoRes = videoDao.getVideo(videoIdx);
            return getVideoRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetVideoRes> getVideosByUser(int userIdx) throws BaseException{
        if(checkUserIdx(userIdx) != 1) {
            throw new BaseException(VIDEO_IDX_NOT_EXISTS);
        }
        try{
            List<GetVideoRes> getVideoRes = videoDao.getVideosByUser(userIdx);
            return getVideoRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetVideoUserHistoryRes> getVideosUserHistory(int userIdx) throws BaseException{
        if(checkUserIdx(userIdx) != 1) {
            throw new BaseException(USER_IDX_NOT_EXISTS);
        }
        try{
            List<GetVideoUserHistoryRes> getVideoUserHistoryRes = videoDao.getVideosUserHistory(userIdx);
            return getVideoUserHistoryRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetVideoRes> getVideosLaterSee(int userIdx) throws BaseException{
        if(checkUserIdx(userIdx) != 1) {
            throw new BaseException(USER_IDX_NOT_EXISTS);
        }
        try{
            List<GetVideoRes> getVideoRes = videoDao.getVideosLaterSee(userIdx);
            return getVideoRes;
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int getUserIdxByVideoIdx(int videoIdx) throws BaseException {
        try{
            return videoDao.getUserIdxByVideoIdx(videoIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public GetVideoLikedRes getVideoLiked(int videoIdx) throws BaseException {
        if (checkVideoIdx(videoIdx) != 1) {
            throw new BaseException(VIDEO_IDX_NOT_EXISTS);
        }
        try{
            return videoDao.getVideoLiked(videoIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public GetVideoUnLikedRes getVideoUnLiked(int videoIdx) throws BaseException {
        if (checkVideoIdx(videoIdx) != 1) {
            throw new BaseException(VIDEO_IDX_NOT_EXISTS);
        }
        try{
            return videoDao.getVideoUnLiked(videoIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public GetVideoCountCommentRes getVideoCountComment(int videoIdx) throws BaseException {
        if (checkVideoIdx(videoIdx) != 1) {
            throw new BaseException(VIDEO_IDX_NOT_EXISTS);
        }
        try{
            return videoDao.getVideoCountComment(videoIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetPlayListRes> getPlayList(int userIdx) throws BaseException {
        if (checkUserIdx(userIdx) != 1) {
            throw new BaseException(USER_IDX_NOT_EXISTS);
        }
        try{
            return videoDao.getPlayList(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetVideoPlayListRes> getVideoPlayList(int playListIdx) throws BaseException {
        if (checkPlayListIdx(playListIdx) != 1) {
            throw new BaseException(USER_IDX_NOT_EXISTS);
        }
        try{
            return videoDao.getVideoPlayList(playListIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    /**
     *  check 관련 함수 모음
     */
    public int checkVideoIdx(int videoIdx) throws BaseException {
        try{
            return videoDao.checkVideoIdx(videoIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkUserIdx(int userIdx) throws BaseException {
        try{
            return videoDao.checkUserIdx(userIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkPlayListIdx(int playListIdx) throws BaseException {
        try{
            return videoDao.checkPlayListIdx(playListIdx);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
