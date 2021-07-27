package com.example.demo.src.user;

import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetUserRes> getUsers() {
        String getUsersQuery = "select * from User";
        return this.jdbcTemplate.query(getUsersQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getString("imageUrl"),
                        rs.getString("name"),
                        rs.getString("birth"),
                        rs.getString("sex"),
                        rs.getString("passwd"),
                        rs.getString("email"),
                        rs.getString("phoneNumber"),
                        rs.getString("status"))
        );
    }
    public List<GetUserRes> getUsersByEmail(String email) {
        String getUsersByEmailQuery = "select * from User where email =?";
        String getUsersByEmailParams = email;
        return this.jdbcTemplate.query(getUsersByEmailQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getString("imageUrl"),
                        rs.getString("name"),
                        rs.getString("birth"),
                        rs.getString("sex"),
                        rs.getString("passwd"),
                        rs.getString("email"),
                        rs.getString("phoneNumber"),
                        rs.getString("status")),
                getUsersByEmailParams);
    }
    public GetUserRes getUserByUserIdx(int userIdx) {
        String getUserQuery = "select * from User where Idx = ?";
        int getUserParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getString("imageUrl"),
                        rs.getString("name"),
                        rs.getString("birth"),
                        rs.getString("sex"),
                        rs.getString("passwd"),
                        rs.getString("email"),
                        rs.getString("phoneNumber"),
                        rs.getString("status")),
                getUserParams);
    }
    public int createUser(PostUserReq postUserReq) {
        String createUserQuery = "insert into User (imageUrl, name, birth, sex, passwd, email, phoneNumber, isKakaoLogin) VALUES (?,?,?,?,?,?,?, ?)";
        Object[] createUserParams = new Object[]{postUserReq.getImageUrl(), postUserReq.getName(), postUserReq.getBirth(),
                postUserReq.getSex(), postUserReq.getPasswd(), postUserReq.getEmail(), postUserReq.getPhoneNumber(), postUserReq.getIsKakaoLogin()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }
    public int logOut(String userJwtToken) {
        String logOutQuery = "insert into DeletedJwtToken (deletedToken) VALUES (?)";
        Object[] logOutParams = new Object[]{userJwtToken};
        this.jdbcTemplate.update(logOutQuery, logOutParams);
        String lastInserIdxQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdxQuery, int.class);
    }
    public User getUserToLogInByEmail(PostLoginReq postLoginReq) {
        String getPwdQuery = "select * from User where email = ?";
        String getPwdParams = postLoginReq.getEmail();     //PostLoginReq 클래스에, @Getter가 있어서 getEmail()가 가능한 것이다.
        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("Idx"),
                        rs.getString("imageUrl"),
                        rs.getString("name"),
                        rs.getString("birth"),
                        rs.getString("sex"),
                        rs.getString("passwd"),
                        rs.getString("email"),
                        rs.getString("phoneNumber")),
                getPwdParams);
    }
    public int modifyUserInfo(PatchUserReq patchUserReq){
        String modifyUserInfoQuery = "UPDATE User SET imageUrl = ?, name = ?, birth = ?, sex = ?, " +
                "passwd = ?, phoneNumber = ?  WHERE Idx = ? ";
        Object[] modifyUserInfoParams = new Object[]{patchUserReq.getImageUrl(), patchUserReq.getName(),
                patchUserReq.getBirth(), patchUserReq.getSex(), patchUserReq.getPasswd(), patchUserReq.getPhoneNumber(),
                patchUserReq.getUserIdx()};
        return this.jdbcTemplate.update(modifyUserInfoQuery,modifyUserInfoParams);
    }
    public int modifyUserStatusInfo(PatchModifyUserStatusReq patchModifyUserStatusReq){
        String modifyUserStatusInfoQuery = "UPDATE User SET status = ? WHERE Idx = ? ";
        Object[] modifyUserStatusInfoParams = new Object[]{patchModifyUserStatusReq.getStatus(), patchModifyUserStatusReq.getUserIdx()};
        return this.jdbcTemplate.update(modifyUserStatusInfoQuery,modifyUserStatusInfoParams);
    }
    public List<GetUserRes> getUserSubs(int userIdx) {
        String getUsersQuery = "select * " +
                "from User " +
                "inner join(select subscribeUserIdx" +
                "                    from SubscribeChannel" +
                "                    where userIdx = ?) UserSub on User.Idx = UserSub.subscribeUserIdx";
        int getUserSubsParams = userIdx;
        return this.jdbcTemplate.query(getUsersQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getString("imageUrl"),
                        rs.getString("name"),
                        rs.getString("birth"),
                        rs.getString("sex"),
                        rs.getString("passwd"),
                        rs.getString("email"),
                        rs.getString("phoneNumber"),
                        rs.getString("status")),
                getUserSubsParams);
    }
    public List<GetUserSearchRes> getUserSearches(int userIdx) {
        String getUserSearchesQuery = "select * from HistorySearch where userIdx = ? order by updatedAt desc";
        int getUserSearchesParams = userIdx;
        return this.jdbcTemplate.query(getUserSearchesQuery,
                (rs, rowNum) -> new GetUserSearchRes(
                        rs.getInt("userIdx"),
                        rs.getString("text")),
                getUserSearchesParams);
    }
    public User getUserByIdx(int userIdx) {
        String getUsersByIdxQuery = "select * from User where Idx =?";
        int getUsersByIdxParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUsersByIdxQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("Idx"),
                        rs.getString("imageUrl"),
                        rs.getString("name"),
                        rs.getString("birth"),
                        rs.getString("sex"),
                        rs.getString("passwd"),
                        rs.getString("email"),
                        rs.getString("phoneNumber")),
                getUsersByIdxParams);
        // RowMapper를 이용한, 람다식
    }
    public List<GetVideosUserLikedRes> getVideosUserLiked(int userIdx) {
        String getVideosUserLikedQuery = "SELECT videoUrl, title, videoExplain, videoTime, kind\n" +
                "FROM Video\n" +
                "         INNER JOIN(SELECT videoIdx, userIdx\n" +
                "                    FROM VideoLike\n" +
                "                    WHERE liked = 1\n" +
                "                    ORDER BY createdAt DESC) VideoLiked ON Video.Idx = VideoLiked.videoIdx\n" +
                "         INNER JOIN(SELECT Idx\n" +
                "                    FROM User\n" +
                "                    WHERE Idx = ?) VideoUser ON VideoLiked.userIdx = VideoUser.Idx";
        int getVideosUserLikedParams = userIdx;
        return this.jdbcTemplate.query(getVideosUserLikedQuery,
                (rs, rowNum) -> new GetVideosUserLikedRes(
                        rs.getString("videoUrl"),
                        rs.getString("title"),
                        rs.getString("videoExplain"),
                        rs.getString("videoTime"),
                        rs.getString("kind")),
                getVideosUserLikedParams);
    }
    public int postSubs(PostSubscribeReq postSubscribeReq) {
        String postSubsQuery = "insert into SubscribeChannel (userIdx, subscribeUserIdx) values(?,?)";
        Object[] postSubsParams = new Object[]{postSubscribeReq.getUserIdx(), postSubscribeReq.getSubscribeUserIdx()};
        this.jdbcTemplate.update(postSubsQuery, postSubsParams);
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }
    public int postSearch(PostSearchReq postSearchReq) {
        String postSearchQuery = "insert into HistorySearch (userIdx, text) values(?,?)";
        Object[] postSearchParams = new Object[]{postSearchReq.getUserIdx(), postSearchReq.getText()};
        this.jdbcTemplate.update(postSearchQuery, postSearchParams);
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }
    public int postVideoHistory(PostVideoHistoryReq postVideoHistoryReq) {
        String postVideoHistoryQuery = "insert into HistoryVideo (userIdx, videoIdx, playTime) values(?,?,?)";
        Object[] postVideoHistoryParams = new Object[]{postVideoHistoryReq.getUserIdx(), postVideoHistoryReq.getVideoIdx(),
                postVideoHistoryReq.getPlayTime()};
        this.jdbcTemplate.update(postVideoHistoryQuery, postVideoHistoryParams);
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }
    public int postLaterVideo(PostLaterVideoReq postLaterVideoReq) {
        String postLaterVideoQuery = "insert into LaterSeeVideo (userIdx, videoIdx) values(?,?)";
        Object[] postLaterVideoParams = new Object[]{postLaterVideoReq.getUserIdx(), postLaterVideoReq.getVideoIdx()};
        this.jdbcTemplate.update(postLaterVideoQuery, postLaterVideoParams);
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery, int.class);
    }

    /**
     * check 관련 함수 모음
     */
    public int checkEmail(String email) {
        String checkEmailQuery = "select exists(select email from User where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);
    }
    public int checkUserIdx(int userIdx) {
        String checkUserIdxQuery = "select exists(select Idx from User where Idx = ?)";
        int checkUserIdxParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserIdxQuery,
                int.class,
                checkUserIdxParams);
    }
    public int checkUserIdxInSearch(int userIdx) {
        String checkUserIdxInSearchQuery = "select exists(select userIdx from HistorySearch where userIdx = ?)";
        int checkUserIdxInSearchParams = userIdx;
        return this.jdbcTemplate.queryForObject(checkUserIdxInSearchQuery,
                int.class,
                checkUserIdxInSearchParams);
    }
    public int checkSubsByUserIdx(PostSubscribeReq postSubscribeReq) {
        String checkSubsByUserIdxQuery = "select exists(select * from SubscribeChannel where userIdx = ? " +
                "&& subscribeUserIdx = ? )";
        int checkSubsByUserParams1 = postSubscribeReq.getUserIdx();
        int checkSubsByUserParams2 = postSubscribeReq.getSubscribeUserIdx();
        return this.jdbcTemplate.queryForObject(checkSubsByUserIdxQuery,
                int.class,
                checkSubsByUserParams1,
                checkSubsByUserParams2);
    }
    public int checkVideoIdx(int vdieoIdx) {
        String checkVideoIdxQuery = "select exists(select Idx from Video where Idx = ?)";
        int checkVideoIdxParams = vdieoIdx;
        return this.jdbcTemplate.queryForObject(checkVideoIdxQuery,
                int.class,
                checkVideoIdxParams);
    }
    public boolean checkDeltedToken(String JwtToken) {
        String checkTokenQuery = "select exists(select Idx from DeletedJwtToken where deletedToken = ?)";
        String checkTokenParams = JwtToken;
        if(this.jdbcTemplate.queryForObject(checkTokenQuery,
                int.class,
                checkTokenParams) == 1) {
            return true;
        }
        else {
            return false;
        }
    }
}
