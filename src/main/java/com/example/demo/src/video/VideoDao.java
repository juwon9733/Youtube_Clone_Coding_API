package com.example.demo.src.video;

import com.example.demo.src.video.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class VideoDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetVideoRes> getVideos(){
        String getVideosQuery = "select * from Video";
        return this.jdbcTemplate.query(getVideosQuery,
                (rs,rowNum) -> new GetVideoRes(
                        rs.getInt("Idx"),
                        rs.getInt("userIdx"),
                        rs.getString("videoUrl"),
                        rs.getString("title"),
                        rs.getString("videoExplain"),
                        rs.getString("videoTime"),
                        rs.getString("kind"))
        );
    }
    public List<GetVideoRes> getVideosByTitle(String title) {
        String getVideosByTitleQuery = "select * from Video where title like ?";
        String getvideosbyTitleParams = "%"+title+"%";
        return this.jdbcTemplate.query(getVideosByTitleQuery,
                (rs,rowNum) -> new GetVideoRes(
                        rs.getInt("Idx"),
                        rs.getInt("userIdx"),
                        rs.getString("videoUrl"),
                        rs.getString("title"),
                        rs.getString("videoExplain"),
                        rs.getString("videoTime"),
                        rs.getString("kind")),
                getvideosbyTitleParams
        );
    }

    public GetVideoRes getVideo(int videoIdx){
        String getVideoQuery = "select * from Video where Idx = ?";
        int getVideoParams = videoIdx;
        return this.jdbcTemplate.queryForObject(getVideoQuery,
                (rs,rowNum) -> new GetVideoRes(
                        rs.getInt("Idx"),
                        rs.getInt("userIdx"),
                        rs.getString("videoUrl"),
                        rs.getString("title"),
                        rs.getString("videoExplain"),
                        rs.getString("videoTime"),
                        rs.getString("kind")),
                getVideoParams
        );
    }

    public int createVideo(PostVideoReq postVideoReq){
        String createVideoQuery = "insert into Video (userIdx, videoUrl, title, videoExplain, videoTime, kind) VALUES (?,?,?,?,?,?)";
        Object[] createVideoParams = new Object[]{postVideoReq.getUserIdx(), postVideoReq.getVideoUrl(),
                postVideoReq.getTitle(), postVideoReq.getVideoExplain(), postVideoReq.getVideoTime(), postVideoReq.getKind()};
        this.jdbcTemplate.update(createVideoQuery, createVideoParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }
    public int modifyVideoStatus(PatchVideoStatusReq patchVideoStatusReq){
        String modifyVideoStatusQuery = "update Video set status = ? where Idx = ? ";
        Object[] modifyVideoStatusParams = new Object[]{patchVideoStatusReq.getStatus(), patchVideoStatusReq.getVideoIdx()};
        return this.jdbcTemplate.update(modifyVideoStatusQuery,modifyVideoStatusParams);
    }
    public int modifyVideo(PatchVideoReq patchVideoReq){
        String modifyVideoQuery = "update Video set videoUrl = ?, title = ?, videoExplain = ?," +
                "videoTime = ?, kind = ? where Idx = ? ";
        Object[] modifyVideoParams = new Object[]{patchVideoReq.getVideoUrl(), patchVideoReq.getTitle(),
                patchVideoReq.getVideoExplain(), patchVideoReq.getVideoTime(),
                patchVideoReq.getKind(), patchVideoReq.getIdx()};
        return this.jdbcTemplate.update(modifyVideoQuery,modifyVideoParams);
    }
    public List<GetVideoRes> getVideosByUser(int userIdx) {
        String getVideosByUsereQuery = "select * from Video where userIdx = ?";
        int getVideosbyUserParams = userIdx;
        return this.jdbcTemplate.query(getVideosByUsereQuery,
                (rs,rowNum) -> new GetVideoRes(
                        rs.getInt("Idx"),
                        rs.getInt("userIdx"),
                        rs.getString("videoUrl"),
                        rs.getString("title"),
                        rs.getString("videoExplain"),
                        rs.getString("videoTime"),
                        rs.getString("kind")),
                getVideosbyUserParams
        );
    }
    public List<GetVideoUserHistoryRes> getVideosUserHistory(int userIdx) {
        String getVideosUserHistoryQuery = "SELECT Idx, userIdx, videoUrl, title, videoExplain, lastPlayTime, kind\n" +
                "FROM Video\n" +
                "         INNER JOIN(SELECT videoIdx, max(playTime) AS lastPlayTime\n" +
                "                    FROM HistoryVideo\n" +
                "                    WHERE userIdx = ?\n" +
                "                    GROUP BY videoIdx\n" +
                ") UserOneHistoryVideo ON Video.Idx = UserOneHistoryVideo.videoIdx";
        int getVideosUserHistoryParams = userIdx;
        return this.jdbcTemplate.query(getVideosUserHistoryQuery,
                (rs,rowNum) -> new GetVideoUserHistoryRes(
                        rs.getInt("Idx"),
                        rs.getInt("userIdx"),
                        rs.getString("videoUrl"),
                        rs.getString("title"),
                        rs.getString("videoExplain"),
                        rs.getString("lastPlayTime"),
                        rs.getString("kind")),
                getVideosUserHistoryParams);
    }
    public List<GetVideoRes> getVideosLaterSee(int userIdx) {
        String getVideosLaterSeeQuery = "select *\n" +
                "from Video\n" +
                "inner join(select videoIdx\n" +
                "    from LaterSeeVideo\n" +
                "    where userIdx = ?) UserOneLaterVideo on Video.Idx = UserOneLaterVideo.videoIdx";
        int getVideosLaterSeeParams = userIdx;
        return this.jdbcTemplate.query(getVideosLaterSeeQuery,
                (rs,rowNum) -> new GetVideoRes(
                        rs.getInt("Idx"),
                        rs.getInt("userIdx"),
                        rs.getString("videoUrl"),
                        rs.getString("title"),
                        rs.getString("videoExplain"),
                        rs.getString("videoTime"),
                        rs.getString("kind")),
                getVideosLaterSeeParams
        );
    }
    public int getUserIdxByVideoIdx(int videoIdx) {
        String getUserIdxByVideoIdxQuery = "select userIdx from Video where Idx = ?";
        int getUserIdxByVideoIdxParams = videoIdx;
        return this.jdbcTemplate.queryForObject(getUserIdxByVideoIdxQuery,
                int.class,
                getUserIdxByVideoIdxParams);
    }
    public GetVideoLikedRes getVideoLiked(int videoIdx) {
        String getVideoLikedQuery = "SELECT userIdx, videoIdx, SUM(liked) AS sumOfLiked\n" +
                "FROM VideoLike\n" +
                "WHERE videoIdx = ? and liked = 1\n" +
                "GROUP BY videoIdx";
        int getVideoLikedParams = videoIdx;
        return this.jdbcTemplate.queryForObject(getVideoLikedQuery,
                (rs,rowNum) -> new GetVideoLikedRes(
                        rs.getInt("userIdx"),
                        rs.getInt("videoIdx"),
                        rs.getString("sumOfLiked")),
                getVideoLikedParams);
    }
    public GetVideoUnLikedRes getVideoUnLiked(int videoIdx) {
        String getVideoUnLikedQuery = "SELECT userIdx, videoIdx, SUM(liked) AS sumOfUnLiked\n" +
                "FROM VideoLike\n" +
                "WHERE videoIdx = ? and liked = -1\n" +
                "GROUP BY videoIdx";
        int getVideoUnLikedParams = videoIdx;
        return this.jdbcTemplate.queryForObject(getVideoUnLikedQuery,
                (rs,rowNum) -> new GetVideoUnLikedRes(
                        rs.getInt("userIdx"),
                        rs.getInt("videoIdx"),
                        rs.getString("sumOfUnLiked")),
                getVideoUnLikedParams);
    }
    public int postVideoLike(PostVideoLikedReq postVideoLikedReq){
        String postVideoLikeQuery = "insert into VideoLike (userIdx, videoIdx, liked) VALUES (?,?,1)";
        Object[] postVideoLikeParams = new Object[]{postVideoLikedReq.getUserIdx(), postVideoLikedReq.getVideoIdx()};
        this.jdbcTemplate.update(postVideoLikeQuery, postVideoLikeParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }
    public int postVideoUnLike(PostVideoUnLikedReq postVideoUnLikedReq){
        String postVideoLikeQuery = "insert into VideoLike (userIdx, videoIdx, liked) VALUES (?,?,-1)";
        Object[] postVideoLikeParams = new Object[]{postVideoUnLikedReq.getUserIdx(), postVideoUnLikedReq.getVideoIdx()};
        this.jdbcTemplate.update(postVideoLikeQuery, postVideoLikeParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }
    public GetVideoCountCommentRes getVideoCountComment(int videoIdx) {
        String getVideoCountCommentQuery = "select videoIdx, count(*) as countOfComment\n" +
                "from Comment\n" +
                "where videoIdx = ?;";
        int getVideoCountCommentParams = videoIdx;
        return this.jdbcTemplate.queryForObject(getVideoCountCommentQuery,
                (rs,rowNum) -> new GetVideoCountCommentRes(
                        rs.getInt("videoIdx"),
                        rs.getString("countOfComment")),
                getVideoCountCommentParams);
    }
    public List<GetPlayListRes> getPlayList(int userIdx) {
        String getPlayListQuery = "SELECT userIdx, playListName\n" +
                "FROM PlayList\n" +
                "WHERE userIdx = ?";
        int getPlayListParams = userIdx;
        return this.jdbcTemplate.query(getPlayListQuery,
                (rs,rowNum) -> new GetPlayListRes(
                        rs.getInt("userIdx"),
                        rs.getString("playListName")),
                getPlayListParams);
    }
    public List<GetVideoPlayListRes> getVideoPlayList(int playListIdx) {
        String getVideoPlayListQuery = "select userIdx, videoUrl, title, videoExplain, videoTime\n" +
                "from Video\n" +
                "         inner join(select videoIdx\n" +
                "                    from ListVideo\n" +
                "                    where playListIdx = ?) VideoList on Video.Idx = VideoList.videoIdx";
        int getVideoPlayListParams = playListIdx;
        return this.jdbcTemplate.query(getVideoPlayListQuery,
                (rs,rowNum) -> new GetVideoPlayListRes(
                        rs.getInt("userIdx"),
                        rs.getString("videoUrl"),
                        rs.getString("title"),
                        rs.getString("videoExplain"),
                        rs.getString("videoTime")),
                getVideoPlayListParams);
    }
    /**
     *  check 관련 함수 모음
     */
    public int checkUserIdx(int userIdx) {
        String checkUserIdxQuery = "select exists(select Idx from User where Idx = ?)";
        int checkUserIdxParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserIdxQuery,
                int.class,
                checkUserIdxParams);
    }
    public int checkVideoIdx(int videoIdx) {
        String checkUserIdxQuery = "select exists(select Idx from Video where Idx = ?)";
        int checkUserIdxParams = videoIdx;
        return this.jdbcTemplate.queryForObject(checkUserIdxQuery,
                int.class,
                checkUserIdxParams);
    }
    public int checkPlayListIdx(int playListIdx) {
        String checPlayListIdxQuery = "select exists(select Idx from PlayList where Idx = ?)";
        int checPlayListIdxParams = playListIdx;
        return this.jdbcTemplate.queryForObject(checPlayListIdxQuery,
                int.class,
                checPlayListIdxParams);
    }
}
