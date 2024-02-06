package com.example.smstest.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 실행 중 발생하는 예외를 처리하기 위한 Class
 */
@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException {
    ErrorCode errorCode;
}