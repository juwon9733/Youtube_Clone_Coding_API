package com.example.demo.src.reply;

import com.example.demo.src.comment.model.GetCommentRes;
import com.example.demo.src.comment.model.PatchCommentReq;
import com.example.demo.src.comment.model.PostCommentReq;
import com.example.demo.src.reply.model.*;
import com.example.demo.src.video.model.GetVideoLikedRes;
import com.example.demo.src.video.model.GetVideoUnLikedRes;
import com.example.demo.src.video.model.PatchVideoStatusReq;
import com.example.demo.src.video.model.PostVideoLikedReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ReplyDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetReplyRes> getReplys(int commentIdx) {
        String getReplysQuery = "select * from CommentReply where commentIdx = ?";
        int getReplysParams = commentIdx;
        return this.jdbcTemplate.query(getReplysQuery,
                (rs,rowNum) -> new GetReplyRes(
                        rs.getInt("Idx"),
                        rs.getInt("userIdx"),
                        rs.getInt("commentIdx"),
                        rs.getString("reply")),
                getReplysParams
        );
    }
    public int createReply(PostReplyReq postReplyReq){
        String createReplyQuery = "insert into CommentReply (userIdx, commentIdx, reply) VALUES (?,?,?)";
        Object[] createReplyParams = new Object[]{postReplyReq.getUserIdx(), postReplyReq.getCommentIdx(),
            postReplyReq.getReply()};
        this.jdbcTemplate.update(createReplyQuery, createReplyParams);
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }
    public int modifyReply(PatchReplyReq patchReplyReq){
        String patchReplyQuery = "update CommentReply set reply = ? where Idx = ? ";
        Object[] patchReplyParams = new Object[]{patchReplyReq.getReply(), patchReplyReq.getReplyIdx()};
        return this.jdbcTemplate.update(patchReplyQuery,patchReplyParams);
    }
    public int modifyReplyStatus(PatchReplyStatusReq patchReplyStatusReq){
        String modifyReplyStatusQuery = "update CommentReply set status = ? where Idx = ? ";
        Object[] modifyReplyStatusParams = new Object[]{patchReplyStatusReq.getStatus(), patchReplyStatusReq.getReplyIdx()};
        return this.jdbcTemplate.update(modifyReplyStatusQuery,modifyReplyStatusParams);
    }
    public GetReplyLikedRes getReplyLiked(int replyIdx) {
        String getReplyLikedQuery = "SELECT Idx, userIdx, replyIdx, SUM(liked) AS sumOfLiked\n" +
                "FROM ReplyLike\n" +
                "WHERE replyIdx = ? && liked = 1\n" +
                "GROUP BY replyIdx";
        int getReplyLikedParams = replyIdx;
        return this.jdbcTemplate.queryForObject(getReplyLikedQuery,
                (rs,rowNum) -> new GetReplyLikedRes(
                        rs.getInt("Idx"),
                        rs.getInt("userIdx"),
                        rs.getInt("replyIdx"),
                        rs.getString("sumOfLiked")),
                getReplyLikedParams);
    }
    public GetReplyUnLikedRes getReplyUnLiked(int replyIdx) {
        String getReplyUnLikedQuery = "SELECT Idx, userIdx, replyIdx, SUM(liked) AS sumOfUnLiked\n" +
                "FROM ReplyLike\n" +
                "WHERE replyIdx = ? && liked = -1\n" +
                "GROUP BY replyIdx";
        int getReplyUnLikedParams = replyIdx;
        return this.jdbcTemplate.queryForObject(getReplyUnLikedQuery,
                (rs,rowNum) -> new GetReplyUnLikedRes(
                        rs.getInt("Idx"),
                        rs.getInt("userIdx"),
                        rs.getInt("replyIdx"),
                        rs.getString("sumOfUnLiked")),
                getReplyUnLikedParams);
    }
    public int postReplyLike(PostReplyLikedReq postReplyLikedReq){
        String postReplyLikeQuery = "insert into ReplyLike (userIdx, replyIdx, liked) VALUES (?,?,1)";
        Object[] postReplyLikeParams = new Object[]{postReplyLikedReq.getUserIdx(), postReplyLikedReq.getReplyIdx()};
        this.jdbcTemplate.update(postReplyLikeQuery, postReplyLikeParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }
    public int postReplyUnLike(PostReplyUnLikedReq postReplyUnLikedReq){
        String postReplyUnLikeQuery = "insert into ReplyLike (userIdx, replyIdx, liked) VALUES (?,?,-1)";
        Object[] postReplyUnLikeParams = new Object[]{postReplyUnLikedReq.getUserIdx(), postReplyUnLikedReq.getReplyIdx()};
        this.jdbcTemplate.update(postReplyUnLikeQuery, postReplyUnLikeParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }
    /**
     * check 관련 함수 모음
     */
    public int checkUserIdx(int userIdx) {
        String checkUserIdxQuery = "select exists(select Idx from User where Idx = ?)";
        int checkUserIdxParams = userIdx;
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
    public int checkReplyIdx(int replyIdx) {
        String checkReplyIdxQuery = "select exists(select Idx from CommentReply where Idx = ?)";
        int checkReplyIdxParams = replyIdx;
        return this.jdbcTemplate.queryForObject(checkReplyIdxQuery,
                int.class,
                checkReplyIdxParams);
    }
    public int checkReplyIdxByUserIdx(PatchReplyReq patchReplyReq) {
        String checkReplyIdxByUserQuery = "select exists(select Idx from CommentReply where Idx = ? and userIdx = ?)";
        int checkReplyIdxByUserParams1 = patchReplyReq.getReplyIdx();
        int checkReplyIdxByUserParams2 = patchReplyReq.getUserIdx();

        return this.jdbcTemplate.queryForObject(checkReplyIdxByUserQuery,
                int.class,
                checkReplyIdxByUserParams1,
                checkReplyIdxByUserParams2);
    }
}

