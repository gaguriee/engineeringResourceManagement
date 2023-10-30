package com.example.smstest.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends RuntimeException {

    @ExceptionHandler(CustomException.class)
    public String CustomException(CustomException e, Model model) {
        model.addAttribute("message", e.getErrorCode().getMessage());

        if (e.getErrorCode().getHttpStatus().equals(HttpStatus.BAD_REQUEST)){
            return "error/400.html";
        }
        else if (e.getErrorCode().getHttpStatus().equals(HttpStatus.FORBIDDEN)){
            return "error/403.html";
        }
        else if (e.getErrorCode().getHttpStatus().equals(HttpStatus.NOT_FOUND)){
            return "error/404.html";
        }
        return "error/500.html";
    }
}