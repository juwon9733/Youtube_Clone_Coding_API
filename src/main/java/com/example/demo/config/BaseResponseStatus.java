package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),

    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    INVALID_USER_EMAIL(false, 2001, "이메일 형식을 확인해주세요"),
    EMPTY_USER_NAME(false, 2002, "유저 이름을 입력해주세요"),
    TOO_LONG_USER_NAME(false, 2003, "유저 이름이 너무 깁니다."),
    EMPTY_USER_BIRTH(false, 2004, "유저 출생일을 입력해주세요"),
    INVALID_USER_BIRTH(false, 2005, "유저 출생일 형식을 확인해주세요"),
    EMPTY_USER_SEX(false, 2006, "유저 성별을 입력해주세요"),
    INVALID_USER_SEX(false, 2007, "유저 성별의 형식을 확인해주세요"),
    EMPTY_USER_PASSWD(false, 2008, "유저 패스워드를 입력해주세요"),
    EMPTY_USER_PHONE_NUM(false, 2009, "유저 핸드폰번호를 입력해주세요"),
    INVALID_USER_PHONE_NUM(false, 2010, "유저 핸드폰번호 형식을 확인해주세요"),
    EMPTY_USER_EMAIL(false, 2011, "유저 이메일을 입력해주세요"),

    EMPTY_USER_IDX(false, 2012, "유저 인덱스 값을 입력해주세요"),

    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    EMPTY_VIDEO_IDX(false, 2004, "비디오 인덱스를 입력해주세요."),
    EMPTY_VIDEO_PLAYTIME(false, 2005, "비디오 플레이 시간을 입력해주세요."),

    EMPTY_VIDEO_URL(false, 2006, "비디오 URL을 입력해주세요"),
    EMPTY_VIDEO_TITLE(false, 2007, "비디오 제목을 입력해주세요"),
    EMPTY_VIDEO_KIND(false, 2008, "비디오 종류를 입력해주세요."),

    EMPTY_VIDEO_STATUS(false, 2009, "비디오 상태 값을 입력해주세요"),
    INVALID_VIDEO_STATUS(false, 2010, "비디오 상태 값을 확인해주세요"),

    COMMENT_IDX_DUPLICATED(false, 2011, "게시글 코멘트인지, 비디오 코멘트인지 확인해주세요"),
    EMPTY_COMMENT(false, 2012, "코멘트를 입력해주세요"),

    EMPTY_COMMENT_STATUS(false, 2013, "코멘트 상태값을 입력해주세요"),
    INVALID_COMMENT_STATUS(false, 2014, "코멘트 형식을 확인해주세요"),

    EMPTY_COMMENT_IDX(false, 2015, "코멘트 인덱스 값을 입력해주세요."),

    EMPTY_REPLY(false, 2016, "답글을 입력해주세요"),
    EMPTY_REPLY_STATUS(false, 2017, "유저 상태값을 입력해주세요"),
    INVALID_REPLY_STATUS(false, 2018, "유저 상태값을 확인해주세요"),

    EMPTY_REPLY_IDX(false, 2019, "답글 인덱스가 존재하지 않습니다."),

    EMPTY_COMMUNITY_CONTENT(false, 2020, "게시글 내용을 입력해주세요."),
    EMPTY_COMMUNITY_IDX(false, 2021, "커뮤니티 게시글 인덱스를 입력해주세요"),
    EMPTY_COMMUNITY_IMAGE(false, 2022, "게시글 이미지를 입력해주세요"),

    EMPTY_USER_STATUS(false, 2023, "유저의 상태를 입력해주세요"),
    INVALID_USER_STATUS(false, 2024, "유저의 상태값을 확인해주세요"),

    EMPYT_KAKAO_LOGIN(false, 2025, "카카오 로그인 여부 값을 입력해주세요"),
    IVALID_KAKAO_LOGIN(false, 2026, "카카오 로그인이면, 카카오 로그입 값을 Y로 보내주세요"),
    /**
     *3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),
    USER_EMAIL_NOT_EXISTS(false, 3001, "유저의 이메일이 존재하지 않습니다."),
    USER_IDX_NOT_EXISTS(false, 3002, "유저 인덱스값이 존재하지 않습니다."),
    VIDEO_IDX_NOT_EXISTS(false, 3003, "비디오 인덱스값이 존재하지 않습니다."),
    POST_IDX_NOT_EXISTS(false, 3004, "게시글 인덱스값이 존재하지 않습니다."),
    COMMENT_IDX_NOT_EXISTS(false, 3005, "댓글 인덱스값이 존재하지 않습니다."),
    REPLY_IDX_NOT_EXISTS(false, 3006, "답글을 인덱스가 존재하지 않습니다."),
    COMM_IDX_NOT_EXISTS(false, 3007, "게시글 인덱스가 존재하지 않습니다."),

    DUPLICATED_EMAIL(false, 3010, "중복되는 이메일입니다."),

    FAILED_TO_LOGIN_BY_PASSWD(false, 3011, "비밀번호가 틀렸습니다."),

    USER_IDX_NOT_EXISTS_IN_SEARCH(false, 3012, "검색기록 테이블에, 해당 유저 인덱스가 존재하지 않습니다."),

    DUPLICATED_SUBS(false, 3013, "이미 구독했습니다."),

    VIDEO_INFO_NOT_USERS_VIDEO(false, 3014, "유저에 해당하는 비디오가 아닙니다."),
    COMMUNITY_IDX_NOT_EXISTS(false, 3015, "해당 게시글 인덱스가 존재하지 않습니다."),

    COMMUNITY_IDX_NOT_USERS_CONTENT(false, 3016, "해당 유저 인덱스 값에 해당하는 게시글이 아닙니다."),
    COMMENT_IDX_NOT_USERS(false, 3017, "해당 유저 인덱스 값에 해당하는 댓글이 아닙니다."),
    REPLY_IDX_NOT_USERS(false, 3018, "해당 유저 인덱스 값에 해당하는 답글이 아닙니다."),

    /**
     * 4000 : Database, Server 오류
     */
    // Common
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    PASSWORD_ENCRYPTION_ERROR(false, 4010, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4015, "비밀번호 복호화에 실패하였습니다."),

    MODIFY_FAIL_USER(false,4020,"유저네임 수정 실패"),

    MODIFY_FAIL_VIDEO(false, 4021, "비디오 정보 수정 실패"),
    MODIFY_FAIL_VIDEO_STATUS(false, 4022, "비디오 상태 수정 실패"),

    MODIFY_FAIL_COMMENT(false, 4023, "코멘트 수정 실패"),
    MODIFY_FAIL_COMMENT_STATUS(false, 4024, "코멘트 상태 수정 실패"),

    MODIFY_FAIL_REPLY(false, 4025, "답글 수정 실패"),
    MODIFY_FAIL_REPLY_STATUS(false, 4026, "답글 상태 수정 실패"),

    MODIFY_FAIL_COMMUNITY(false, 4027, "게시글 정보 수정 실패"),
    MODIFY_FAIL_COMMUNITY_STATUS(false, 4028, "게시글 상태 수정 실패"),

    MODIFY_FAIL_USER_STATUS(false, 3019, "유저 상태 정보 변경 실패"),

    // 5000 : 필요시 만들어서 쓰세요
    ERROR_ONE(false, 5001, "카카오 소셜 로그인 에러1"),
    ERROR_TOW(false, 5002, "카카오 소셜 로그인 에러2"),

    // 6000 : 필요시 만들어서 쓰세요
    DELETED_TOKEN(false, 5003, "삭제된 토큰으로, 재 로그인 해야합니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
