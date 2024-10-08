package com.kkokkomu.short_news.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //Bad Request Error
    INVALID_PARAMETER("40000", HttpStatus.BAD_REQUEST, "유효하지 않는 파라미터입니다."),
    MISSING_REQUEST_PARAMETER("40001", HttpStatus.BAD_REQUEST, "필수 파라미터가 누락되었습니다."),
    INVALID_ROLE("40002", HttpStatus.BAD_REQUEST, "유효하지 않은 권한입니다."),
    INVALID_PROVIDER("40003", HttpStatus.BAD_REQUEST, "유효하지 않은 제공자입니다."),
    INVALID_HEADER("40004", HttpStatus.BAD_REQUEST, "유효하지 않은 헤더값입니다."),
    DUPLICATED_SERIAL_ID("40005", HttpStatus.BAD_REQUEST, "해당 아이디로 가입된 계정이 존재합니다."),
    PASSWORD_NOT_MATCH("40006", HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    DUPLICATED_NICKNAME("40007", HttpStatus.BAD_REQUEST, "중복된 닉네임입니다."),
    MISSING_REQUEST_BODY("40008", HttpStatus.BAD_REQUEST, "요청 바디가 누락되었습니다."),
    MISSING_REQUEST_IMAGES("40009", HttpStatus.BAD_REQUEST, "이미지가 누락되었습니다."),
    INVALID_APPLE_IDENTITY_TOKEN_ERROR("40010", HttpStatus.BAD_REQUEST, "유효하지 않은 Apple Identity Token입니다."),
    EXPIRED_APPLE_IDENTITY_TOKEN_ERROR("40011", HttpStatus.BAD_REQUEST, "만료된 Apple Identity Token입니다."),
    INVALID_APPLE_PUBLIC_KEY_ERROR("40012", HttpStatus.BAD_REQUEST, "유효하지 않은 Apple Public Key입니다."),
    INVALID_OAUTH2_PROVIDER("40013", HttpStatus.BAD_REQUEST, "유효하지 않은 OAuth2 제공자입니다."),
    DELETED_USER_ERROR("40014", HttpStatus.BAD_REQUEST, "이미 탈퇴한 유저입니다."),
    DUPLICATED_SOCIAL_ID("40015", HttpStatus.BAD_REQUEST, "해당 이메일로 가입된 소셜 계정이 존재합니다."),
    CANNOT_BLOCK_MYSELF("40016", HttpStatus.BAD_REQUEST, "자신을 차단할 수 없습니다."),
    ALREADY_BLOCKED_USER("40017", HttpStatus.BAD_REQUEST, "이미 차단된 사용자입니다."),
    ALREADY_REGISTERED_KEYWORD("40018", HttpStatus.BAD_REQUEST, "이미 등록된 키워드입니다."),
    DUPLICATED_KEYWORD("40019", HttpStatus.BAD_REQUEST, "이미 생성된 키워드입니다."),
    INVALID_KEYWORD("40020", HttpStatus.BAD_REQUEST, "키워드는 2글자이상 20글자이하 알파벳/한글/숫자로 구성된 한 단어가 되어야합니다."),
    DUPLICATED_COMMENT_LIKE("40021", HttpStatus.BAD_REQUEST, "이미 좋아요를 단 댓글입니다."),
    INVALID_COMMENT_CURSOR("40022", HttpStatus.BAD_REQUEST, "요청하신 커서 id에 해당하는 댓글이 존재하지 않습니다."),
    DUPLICATED_NEWS_REACTION("40023", HttpStatus.BAD_REQUEST, "이미 감정표현을 한 뉴스입니다."),
    INVALID_CATEGORY_SELECTION("40024", HttpStatus.BAD_REQUEST, "모든 카테고리가 false일 수는 없습니다"),
    INVALID_CATEGORY_CONCAT("40025", HttpStatus.BAD_REQUEST, "요청과 매칭 되는 카테고리가 존재하지 않습니다."),
    INVALID_HIDE_USER("40026", HttpStatus.BAD_REQUEST, "자기 자신을 차단할 수 없습니다."),
    DUPLICATED_HIDE_USER("40027", HttpStatus.BAD_REQUEST, "이미 차단한 유저입니다."),
    DUPLICATED_REPORTED_COMMENT("40028", HttpStatus.BAD_REQUEST, "이미 신고한 댓글입니다."),
    BANNED_USER_COMMENT("40029", HttpStatus.BAD_REQUEST, "댓글 기능이 정지된 유저입니다."),
    ALREADY_EXECUTED_COMMENT("40030", HttpStatus.BAD_REQUEST, "이미 처리된 댓글 신고입니다."),
    ALREADY_EXECUTED_NEWS("40031", HttpStatus.BAD_REQUEST, "이미 처리된 뉴스 신고입니다."),
    DUPLICATED_REPORTED_NEWS("40032", HttpStatus.BAD_REQUEST, "이미 신고한 뉴스입니다."),

    // Unauthorized Error
    FAILURE_LOGIN("40100", HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다."),
    FAILURE_LOGOUT("40101", HttpStatus.UNAUTHORIZED, "로그아웃에 실패했습니다."),
    INVALID_TOKEN_ERROR("40102", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN_ERROR("40103", HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    TOKEN_MALFORMED_ERROR("40104", HttpStatus.UNAUTHORIZED, "토큰이 올바르지 않습니다."),
    TOKEN_TYPE_ERROR("40105", HttpStatus.UNAUTHORIZED, "토큰 타입이 일치하지 않습니다."),
    TOKEN_UNSUPPORTED_ERROR("40106", HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰입니다."),
    TOKEN_GENERATION_ERROR("40107", HttpStatus.UNAUTHORIZED, "토큰 생성에 실패하였습니다."),
    TOKEN_UNKNOWN_ERROR("40108", HttpStatus.UNAUTHORIZED, "알 수 없는 토큰입니다."),
    EMPTY_AUTHENTICATION("40109", HttpStatus.UNAUTHORIZED, "인증 정보가 없습니다."),
    INVALID_ADMIN_ROLE("40110", HttpStatus.UNAUTHORIZED, "해당 유저에 대한 관리자 role이 존재하지 않습니다"),

    // Access Denied Error
    ACCESS_DENIED_ERROR("40300", HttpStatus.FORBIDDEN, "액세스 권한이 없습니다."),

    // Not Found Error
    NOT_FOUND_USER("40400", HttpStatus.NOT_FOUND, "해당 사용자가 존재하지 않습니다."),
    NOT_FOUND_END_POINT("40401", HttpStatus.NOT_FOUND, "존재하지 않는 엔드포인트입니다."),
    NOT_FOUND_RESOURCE("40402", HttpStatus.NOT_FOUND, "요청한 데이터를 찾을 수 없습니다."),
    NOT_FOUND_KEYWORD("40403", HttpStatus.NOT_FOUND, "해당 키워드가 존재하지 않습니다."),
    NOT_FOUND_USER_KEYWORD("40404", HttpStatus.NOT_FOUND, "해당 유저 키워드가 존재하지 않습니다."),
    NOT_FOUND_PROFILE_IMG("40405", HttpStatus.NOT_FOUND, "해당 프로필 사진이 존재하지 않습니다."),
    NOT_FOUND_SUBSCRIPTION("40406", HttpStatus.NOT_FOUND, "해당 사용자 구독 정보가 존재하지 않습니다."),
    NOT_FOUND_NEWS("40407", HttpStatus.NOT_FOUND, "해당 뉴스가 존재하지 않습니다."),
    NOT_FOUND_COMMENT("40408", HttpStatus.NOT_FOUND, "해당 댓글이 존재하지 않습니다."),
    NOT_FOUND_COMMENT_LIKE("40409", HttpStatus.NOT_FOUND, "해당 댓글 좋아요가 존재하지 않습니다."),
    NOT_FOUND_PARENT_COMMENT("40410", HttpStatus.NOT_FOUND, "해당 부모 댓글이 존재하지 않습니다."),
    NOT_FOUND_REPLY("40411", HttpStatus.NOT_FOUND, "해당 대댓글이 존재하지 않습니다."),
    NOT_FOUND_CURSOR("40412", HttpStatus.NOT_FOUND, "해당 커서가 존재하지 않습니다."),
    NOT_FOUND_NEWS_REACTION("40413", HttpStatus.NOT_FOUND, "해당 뉴스 감정표현이 존재하지 않습니다."),
    NOT_FOUND_TARGET_USER("40414", HttpStatus.NOT_FOUND, "신고하려는 유저가 존재하지 않습니다."),
    NOT_FOUND_HIDE_USER("40415", HttpStatus.NOT_FOUND, "신고 유저 내역이 존재하지 않습니다."),
    NOT_FOUND_REPORTED_COMMENT("40416", HttpStatus.NOT_FOUND, "신고 댓글 내역이 존재하지 않습니다."),
    NOT_FOUND_ADMIN("40417", HttpStatus.NOT_FOUND, "해당 관리자가 존재하지 않습니다."),
    NOT_FOUND_REPORTED_NEWS("40418", HttpStatus.NOT_FOUND, "신고 뉴스 내역이 존재하지 않습니다."),

    // UnsupportedMediaType Error
    UNSUPPORTED_MEDIA_TYPE("41500", HttpStatus.UNSUPPORTED_MEDIA_TYPE, "허용되지 않은 파일 형식입니다."),

    // Server, File Up/DownLoad Error
    SERVER_ERROR("50000", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    S3_PROCESSING_ERROR("50001", HttpStatus.INTERNAL_SERVER_ERROR, "s3 파일 업로드에 실패했습니다."),
    VIDEO_SERVER_ERROR("50002", HttpStatus.INTERNAL_SERVER_ERROR, "비디오 생성에 실패했습니다"),
    MAIL_SEND_ERROR("50003", HttpStatus.INTERNAL_SERVER_ERROR, "메일 전송에 실패하였습니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
