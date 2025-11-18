package com.hufs.dongri.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseErrorCode {

    // == 기본(공통) 에러 ==
    INVALID_REQUEST("COMMON400", "요청이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("COMMON401", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("COMMON403", "접근이 금지되었습니다.", HttpStatus.FORBIDDEN),
    NOT_FOUND("COMMON404", "요청한 자원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR("COMMON500", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // == Auth (인증/권한) 관련 에러 ==
    EMAIL_DUPLICATED("AUTH409_1", "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    STUDENT_SHOULD_USE_OAUTH("AUTH400_1", "학생은 [Google로 로그인]을 이용해주세요.", HttpStatus.BAD_REQUEST),
    ACCOUNT_PENDING_APPROVAL("AUTH403_1", "아직 승인 대기 중인 계정입니다.", HttpStatus.FORBIDDEN),
    OAUTH_EMAIL_NOT_HUFS("AUTH403_2", "한국외대 메일(@hufs.ac.kr)로만 로그인할 수 있습니다.", HttpStatus.FORBIDDEN),

    // == User (사용자) 관련 에러 ==
    USER_NOT_FOUND("USER404_1", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    STUDENT_ID_DUPLICATED("USER409_1", "이미 등록된 학번입니다.", HttpStatus.CONFLICT),

    // == Club (동아리) 관련 에러 ==
    CLUB_NOT_FOUND("CLUB404_1", "해당 동아리를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // == Application (신청서) 관련 에러 ==
    APPLICATION_NOT_FOUND("APP404_1", "신청서를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    APPLICATION_ALREADY_PENDING("APP409_1", "이미 처리 대기 중인 신청서가 존재합니다.", HttpStatus.CONFLICT),
    ALREADY_MEMBER("APP409_2", "이미 해당 동아리에 가입된 회원입니다.", HttpStatus.CONFLICT),
    APPLICATION_ALREADY_PROCESSED("APP400_2", "이미 처리된 신청서입니다.", HttpStatus.BAD_REQUEST),

    // == Operator (운영진) 관련 에러 ==
    NOT_CLUB_OPERATOR("OPER403_1", "해당 동아리의 운영진이 아닙니다.", HttpStatus.FORBIDDEN);


    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}