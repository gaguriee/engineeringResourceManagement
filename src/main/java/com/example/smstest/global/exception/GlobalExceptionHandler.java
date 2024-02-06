package com.example.smstest.global.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 전역 에러 핸들러, 서버 발생 오류를 필터링 후 로깅 또는 에러 페이지를 반환
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends RuntimeException {

    /**
     * DB 내 예외와 관련된 에러 처리
     * @param e
     * @param model
     * @param request
     * @return
     * @throws IOException
     */
    @ExceptionHandler(DataAccessResourceFailureException.class)
    public String handleJDBCConnectionException(DataAccessResourceFailureException e, Model model, HttpServletRequest request) throws IOException {
        model.addAttribute("message", "데이터베이스 연결에 문제가 있습니다. 잠시 후 다시 시도해주세요.");

        Map<String, Object> params = getParams(request);

        String taskSummary = (String) params.get("taskSummary");
        model.addAttribute("taskSummary", taskSummary);

        String taskDetails = (String) params.get("taskDetails");
        model.addAttribute("taskDetails", taskDetails);

        String serverIp = InetAddress.getLocalHost().getHostAddress();
        Object requestBody = new ObjectMapper().readTree(request.getInputStream().readAllBytes());

        model.addAttribute("requestBody", requestBody);
        model.addAttribute("params", params);

        // Request Body, Params, URI, Method, Server IP와 에러 내용을 함께 출력

        ReqResLogging reqResLogging = new ReqResLogging(
                "UnhandledException",
                request.getMethod(),
                request.getRequestURI(),
                params,
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                serverIp,
                requestBody,
                ErrorCode.SERVER_ERROR,
                e.getMessage(),
                SecurityContextHolder.getContext().getAuthentication().getName()
        );

        e.printStackTrace();

        log.error(reqResLogging.toString());

        return "error/500";

    }

    /**
     * 사전 정의한 Custom 예외 처리, 로깅 후 에러 페이지 반환
     * @param e
     * @param model
     * @param request
     * @return
     * @throws IOException
     */
    @ExceptionHandler(CustomException.class)
    public String CustomException(CustomException e, Model model, HttpServletRequest request) throws IOException {
        model.addAttribute("message", e.getErrorCode().getMessage());

        // 현재 요청과 관련된 고유한 식별자
        Map<String, Object> params = getParams(request);

        String taskSummary = (String) params.get("taskSummary");
        model.addAttribute("taskSummary", taskSummary);

        String taskDetails = (String) params.get("taskDetails");
        model.addAttribute("taskDetails", taskDetails);

        String serverIp = InetAddress.getLocalHost().getHostAddress();
        Object requestBody = new ObjectMapper().readTree(request.getInputStream().readAllBytes());

        model.addAttribute("request", requestBody);

        // Request Body, Params, URI, Method, Server IP와 에러 내용을 함께 출력

        ReqResLogging reqResLogging = new ReqResLogging(
                "customException",
                request.getMethod(),
                request.getRequestURI(),
                params,
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                serverIp,
                requestBody,
                e.getErrorCode(),
                e.getErrorCode().getMessage(),
                SecurityContextHolder.getContext().getAuthentication().getName()
        );

        log.info(reqResLogging.toString());

        if (e.getErrorCode().getHttpStatus().equals(HttpStatus.BAD_REQUEST)) {
            return "error/400.html";
        } else if (e.getErrorCode().getHttpStatus().equals(HttpStatus.FORBIDDEN)) {
            return "error/403.html";
        } else if (e.getErrorCode().getHttpStatus().equals(HttpStatus.NOT_FOUND)) {
            return "error/404.html";
        }

        return "error/500.html";
    }

    /**
     * 파일 요청 시 발생할 수 있는 예외 처리
     * @param e
     * @param model
     * @param request
     * @return
     * @throws IOException
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleMultipartException(Exception e, Model model, HttpServletRequest request) throws IOException {
        // 현재 요청과 관련된 고유한 식별자
        Map<String, Object> params = getParams(request);

        String taskSummary = (String) params.get("taskSummary");
        model.addAttribute("taskSummary", taskSummary);

        String taskDetails = (String) params.get("taskDetails");
        model.addAttribute("taskDetails", taskDetails);

        String serverIp = InetAddress.getLocalHost().getHostAddress();
        Object requestBody = new ObjectMapper().readTree(request.getInputStream().readAllBytes());

        model.addAttribute("requestBody", requestBody);
        model.addAttribute("params", params);

        // Request Body, Params, URI, Method, Server IP와 에러 내용을 함께 출력

        ReqResLogging reqResLogging = new ReqResLogging(
                "UnhandledException",
                request.getMethod(),
                request.getRequestURI(),
                params,
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                serverIp,
                requestBody,
                ErrorCode.SERVER_ERROR,
                e.getMessage(),
                SecurityContextHolder.getContext().getAuthentication().getName()
        );

        e.printStackTrace();

        log.error(reqResLogging.toString());

        // 에러 페이지로 포워딩
        return "error/500";
    }

    // 파라미터 가져오기
    public static Map<String, Object> getParams(HttpServletRequest request) {
        Map<String, Object> jsonObject = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String replaceParam = paramName.replace("\\.", "-");
            jsonObject.put(replaceParam, request.getParameter(paramName));
        }
        return jsonObject;
    }
}