package com.example.smstest.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    /* 400 BAD_REQUEST : 잘못된 요청 */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    FILE_SAVE_ERROR(HttpStatus.BAD_REQUEST, "디스크 공간 부족 또는 기타 파일 시스템 오류입니다."),
    DATE_INVALID(HttpStatus.BAD_REQUEST, "7일 이내의 내역만 업로드 할 수 있습니다."),

    /* 403 FORBIDDEN : 인증되지 않은 사용자 */
    INVALID_AUTHORITY(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    /* 404 NOT_FOUND : Resource를 찾을 수 없음 */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 정보의 사용자를 찾을 수 없습니다."),
    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 소속을 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 게시글을 찾을 수 없습니다."),
    CLIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 고객사를 찾을 수 없습니다."),
    TASK_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 일정을 찾을 수 없습니다."),
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 데이터를 찾을 수 없습니다."),
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 파일을 다운로드할 수 없습니다."),
    ORGANIZATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 소속을 찾을 수 없습니다."),

    /* 409 : CONFLICT : Resource의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "데이터가 이미 존재합니다."),


//    500
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다");


    private final HttpStatus httpStatus;
    private final String message;
}