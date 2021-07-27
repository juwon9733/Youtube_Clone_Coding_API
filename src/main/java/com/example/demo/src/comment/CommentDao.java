package com.example.demo.src.comment;

import com.example.demo.src.comment.model.*;
import com.example.demo.src.video.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CommentDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetCommentRes> getComments(){
        String getCommentsQuery = "select * from Comment";
        return this.jdbcTemplate.query(getCommentsQuery,
                (rs,rowNum) -> new GetCommentRes(
                        rs.getInt("Idx"),
                        rs.getInt("videoIdx"),
                        rs.getInt("postIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("comment"))
        );
    }
    public List<GetCommentRes> getCommentsVideo(int videoIdx){
        String getCommentsVideoQuery = "select * from Comment where videoIdx = ? and videoIdx is not null";
        int getCommentsVideoParams = videoIdx;
        return this.jdbcTemplate.query(getCommentsVideoQuery,
                (rs,rowNum) -> new GetCommentRes(
                        rs.getInt("Idx"),
                        rs.getInt("videoIdx"),
                        rs.getInt("postIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("comment")),
                getCommentsVideoParams
        );
    }

    public List<GetCommentRes> getCommentsPost(int postIdx){
        String getCommentsPostQuery = "select * from Comment where postIdx = ? and postIdx is not null";
        int getCommentsPostParams = postIdx;
        return this.jdbcTemplate.query(getCommentsPostQuery,
                (rs,rowNum) -> new GetCommentRes(
                        rs.getInt("Idx"),
                        rs.getInt("videoIdx"),
                        rs.getInt("postIdx"),
                        rs.getInt("userIdx"),
                        rs.getString("comment")),
                getCommentsPostParams
        );
    }
    public int createComment(PostCommentReq postCommentReq){
        String createCommentQuery = "INSERT INTO Comment (videoIdx, postIdx, userIdx, comment) VALUES (?,?,?,?)";
        Object[] createCommentParams = new Object[]{postCommentReq.getVideoIdx(), postCommentReq.getPostIdx(),
                postCommentReq.getUserIdx(), postCommentReq.getComment()};
        this.jdbcTemplate.update(createCommentQuery, createCommentParams);
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }
    public int modifyComment(PatchCommentReq patchCommentReq){
        String patchCommentQuery = "update Comment set comment = ? where Idx = ? ";
        Object[] patchCommentParams = new Object[]{patchCommentReq.getComment(), patchCommentReq.getIdx()};

        return this.jdbcTemplate.update(patchCommentQuery,patchCommentParams);
    }
    public int modifyCommentStatus(PatchCommentStatusReq patchCommentStatusReq){
        String modifyCommentStatusQuery = "update Comment set status = ? where Idx = ? ";
        Object[] modifyCommentStatusParams = new Object[]{patchCommentStatusReq.getStatus(), patchCommentStatusReq.getIdx()};
        return this.jdbcTemplate.update(modifyCommentStatusQuery,modifyCommentStatusParams);
    }
    public GetCommentLikedRes getCommentLiked(int commentIdx) {
        String getCommentLikedQuery = "SELECT commentIdx, SUM(liked) AS sumOfLiked\n" +
                "FROM CommentLike\n" +
                "WHERE commentIdx = ? and liked = 1\n" +
                "GROUP BY commentIdx";
        int getCommentLikedParams = commentIdx;
        return this.jdbcTemplate.queryForObject(getCommentLikedQuery,
                (rs,rowNum) -> new GetCommentLikedRes(
                        rs.getInt("commentIdx"),
                        rs.getString("sumOfLiked")),
                getCommentLikedParams);
    }
    public GetCommentUnLikedRes getCommentUnLiked(int commentIdx) {
        String getCommentUnLikedQuery = "SELECT Idx, userIdx, commentIdx, SUM(liked) AS sumOfUnLiked\n" +
                "FROM CommentLike\n" +
                "WHERE commentIdx = ? and liked = -1\n" +
                "GROUP BY commentIdx";
        int getCommentUnLikedParams = commentIdx;
        return this.jdbcTemplate.queryForObject(getCommentUnLikedQuery,
                (rs,rowNum) -> new GetCommentUnLikedRes(
                        rs.getInt("Idx"),
                        rs.getInt("userIdx"),
                        rs.getInt("commentIdx"),
                        rs.getString("sumOfUnLiked")),
                getCommentUnLikedParams);
    }
    public int postCommentLike(PostCommentLikedReq postCommentLikedReq){
        String postCommentLikeQuery = "insert into CommentLike (userIdx, commentIdx, liked) VALUES (?,?,1)";
        Object[] postCommentLikeParams = new Object[]{postCommentLikedReq.getUserIdx(), postCommentLikedReq.getCommentIdx()};
        this.jdbcTemplate.update(postCommentLikeQuery, postCommentLikeParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }
    public int postCommentUnLike(PostCommentUnLikedReq postCommentUnLikedReq){
        String postCommentUnLikeQuery = "insert into CommentLike (userIdx, commentIdx, liked) VALUES (?,?,-1)";
        Object[] postCommentUnLikeParams = new Object[]{postCommentUnLikedReq.getUserIdx(), postCommentUnLikedReq.getCommentIdx()};
        this.jdbcTemplate.update(postCommentUnLikeQuery, postCommentUnLikeParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }
    public GetCommentCountReplyRes getCommentCountReply(int commentIdx) {
        String getCommentCountCommentQuery = "select commentIdx, count(*) as countOfReply\n" +
                "from CommentReply\n" +
                "where commentIdx = ?";
        int getCommentCountCommentParams = commentIdx;
        return this.jdbcTemplate.queryForObject(getCommentCountCommentQuery,
                (rs,rowNum) -> new GetCommentCountReplyRes(
                        rs.getInt("commentIdx"),
                        rs.getString("countOfReply")),
                getCommentCountCommentParams);
    }
    /**
     * check 관련 함수 모음
     */
    public int checkVideoIdx(int videoIdx) {
        String checkUserIdxQuery = "select exists(select Idx from Video where Idx = ?)";
        int checkUserIdxParams = videoIdx;
        return this.jdbcTemplate.queryForObject(checkUserIdxQuery,
                int.class,
                checkUserIdxParams);
    }
    public int checkPostIdx(int postIdx) {
        String checkUserIdxQuery = "select exists(select Idx from Post where Idx = ?)";
        int checkUserIdxParams = postIdx;
        return this.jdbcTemplate.queryForObject(checkUserIdxQuery,
                int.class,
                checkUserIdxParams);
    }
    public int checkCommentIdx(int commentIdx) {
        String checkCommentIdxQuery = "select exists(select Idx from Comment where Idx = ?)";
        int checkComnetIdxParams = commentIdx;
        return this.jdbcTemplate.queryForObject(checkCommentIdxQuery,
                int.class,
                checkComnetIdxParams);
    }
    public int checkCommnetIdxByUserIdx(PatchCommentReq patchCommentReq) {
        String checkCommentIdxByUserIdxQuery = "select exists(select Idx from Comment where Idx = ? and userIdx = ?)";
        int checkComnetIdxByUserIdxParams1 = patchCommentReq.getIdx();
        int checkComnetIdxByUserIdxParams2 = patchCommentReq.getUserIdx();
        return this.jdbcTemplate.queryForObject(checkCommentIdxByUserIdxQuery,
                int.class,
                checkComnetIdxByUserIdxParams1,
                checkComnetIdxByUserIdxParams2);
    }
}
