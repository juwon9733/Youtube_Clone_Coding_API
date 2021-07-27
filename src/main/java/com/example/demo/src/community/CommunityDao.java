package com.example.demo.src.community;

import com.example.demo.src.comment.model.GetCommentLikedRes;
import com.example.demo.src.comment.model.PatchCommentReq;
import com.example.demo.src.comment.model.PostCommentLikedReq;
import com.example.demo.src.comment.model.PostCommentReq;
import com.example.demo.src.community.model.*;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.src.video.model.GetVideoCountCommentRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CommunityDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetCommunityRes> getCommunity(int userIdx) {
        String getCommunityQuery = "select userIdx, content from CommunityPosting where userIdx = ?";
        int getCommunityParams = userIdx;
        return this.jdbcTemplate.query(getCommunityQuery,
                (rs, rowNum) -> new GetCommunityRes(
                        rs.getInt("userIdx"),
                        rs.getString("content")),
                getCommunityParams
        );
    }
    public List<GetCommPictureRes> getCommPicture(int commIdx) {
        String getCommPictureQuery = "select postIdx, imageUrl from PostImageUrl where postIdx = ?";
        int getCommPictureParams = commIdx;
        return this.jdbcTemplate.query(getCommPictureQuery,
                (rs, rowNum) -> new GetCommPictureRes(
                        rs.getInt("postIdx"),
                        rs.getString("imageUrl")),
                getCommPictureParams
        );
    }
    public int postCommContent(PostCommContentReq postCommContentReq){
        String postCommContentQuery = "insert into CommunityPosting (userIdx, content) VALUES (?,?)";
        Object[] postCommContentParams = new Object[]{postCommContentReq.getUserIdx(), postCommContentReq.getContent()};
        this.jdbcTemplate.update(postCommContentQuery, postCommContentParams);
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }
    public int postCommPicture(PostCommPictureReq postCommPictureReq){
        String postCommPictureQuery = "insert into PostImageUrl (postIdx, imageUrl) VALUES (?,?)";
        Object[] postCommPictureParams = new Object[]{postCommPictureReq.getCommIdx(), postCommPictureReq.getImageUrl()};
        this.jdbcTemplate.update(postCommPictureQuery, postCommPictureParams);
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }
    public int modifyCommunity(PatchCommunityReq patchCommunityReq){
        String patchCommunityQuery = "update CommunityPosting set content = ? where Idx = ? ";
        Object[] patchCommunityParams = new Object[]{patchCommunityReq.getContent(), patchCommunityReq.getCommIdx()};

        return this.jdbcTemplate.update(patchCommunityQuery,patchCommunityParams);
    }
    public int modifyCommunityStatus(PatchCommunityStatusReq patchCommunityStatusReq){
        String patchCommunityStatusQuery = "update CommunityPosting set status = ? where Idx = ? ";
        Object[] patchCommunityStatusParams = new Object[]{patchCommunityStatusReq.getStatus(), patchCommunityStatusReq.getIdx()};

        return this.jdbcTemplate.update(patchCommunityStatusQuery,patchCommunityStatusParams);
    }
    public GetCommunityLikedRes getCommunityLiked(int commIdx) {
        String getCommunityLikedQuery = "SELECT postIdx, SUM(liked) AS sumOfLiked\n" +
                "FROM PostLike\n" +
                "WHERE postIdx = ? and liked = 1\n" +
                "GROUP BY postIdx";
        int getCommunityLikedParams = commIdx;
        return this.jdbcTemplate.queryForObject(getCommunityLikedQuery,
                (rs,rowNum) -> new GetCommunityLikedRes(
                        rs.getInt("postIdx"),
                        rs.getString("sumOfLiked")),
                getCommunityLikedParams);
    }
    public GetCommunityUnLikedRes getCommunityUnLiked(int commIdx) {
        String getCommunityUnLikedQuery = "SELECT postIdx, SUM(liked) AS sumOfUnLiked\n" +
                "FROM PostLike\n" +
                "WHERE postIdx = ? and liked = -1\n" +
                "GROUP BY postIdx";
        int getCommunityUnLikedParams = commIdx;
        return this.jdbcTemplate.queryForObject(getCommunityUnLikedQuery,
                (rs,rowNum) -> new GetCommunityUnLikedRes(
                        rs.getInt("postIdx"),
                        rs.getString("sumOfUnLiked")),
                getCommunityUnLikedParams);
    }
    public int postCommunityLike(PostCommunityLikedReq postCommunityLikedReq){
        String postCommunityLikeQuery = "insert into PostLike (userIdx, postIdx, liked) VALUES (?,?,1)";
        Object[] postCommunityLikeParams = new Object[]{postCommunityLikedReq.getUserIdx(), postCommunityLikedReq.getPostIdx()};
        this.jdbcTemplate.update(postCommunityLikeQuery, postCommunityLikeParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }
    public int postCommunityUnLike(PostCommunityUnLikedReq postCommunityUnLikedReq){
        String postCommunityUnLikeQuery = "insert into PostLike (userIdx, postIdx, liked) VALUES (?,?,-1)";
        Object[] postCommunityUnLikeParams = new Object[]{postCommunityUnLikedReq.getUserIdx(), postCommunityUnLikedReq.getPostIdx()};
        this.jdbcTemplate.update(postCommunityUnLikeQuery, postCommunityUnLikeParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }
    public GetCommunityCountCommentRes getCommunityCountComment(int commIdx) {
        String getCommunityCountCommentQuery = "select postIdx, count(*) as countOfComment\n" +
                "from Comment\n" +
                "where postIdx = ?;";
        int getCommunityCountCommentParams = commIdx;
        return this.jdbcTemplate.queryForObject(getCommunityCountCommentQuery,
                (rs,rowNum) -> new GetCommunityCountCommentRes(
                        rs.getInt("postIdx"),
                        rs.getString("countOfComment")),
                getCommunityCountCommentParams);
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
    public int checkCommIdx(int commIdx) {
        String checkCommIdxQuery = "select exists(select Idx from CommunityPosting where Idx = ?)";
        int checkCommIdxParams = commIdx;
        return this.jdbcTemplate.queryForObject(checkCommIdxQuery,
                int.class,
                checkCommIdxParams);
    }
    public int checkCommIdxByUserIdx(PatchCommunityReq patchCommunityReq) {
        String checkCommIdxByUserIdxQuery = "select exists(select Idx from CommunityPosting where Idx = ? and userIdx = ?)";
        int checkCommIdxByUserIdxParams1 = patchCommunityReq.getCommIdx();
        int checkCommIdxByUserIdxParams2 = patchCommunityReq.getUserIdx();
        return this.jdbcTemplate.queryForObject(checkCommIdxByUserIdxQuery,
                int.class,
                checkCommIdxByUserIdxParams1,
                checkCommIdxByUserIdxParams2);
    }
}
