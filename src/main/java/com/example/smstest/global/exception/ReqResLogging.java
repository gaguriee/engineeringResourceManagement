package com.example.smstest.global.exception;

/**
 * trace_id: request, response를 pair로 묶는 uuid
 * request 기본 정보: uri, http method, request server ip, device type
 * 서버 비즈니스 로직 정보: controller, method
 * 요청 데이터: query parameter, request body
 * 응답 데이터: response body, 응답 경과 시간
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ReqResLogging {

    @JsonProperty(value = "error_type")
    private String errorType;

    @JsonProperty(value = "http_method")
    private String httpMethod;

    @JsonProperty(value = "uri")
    private String uri;

    @JsonProperty(value = "params")
    private Map<String, Object> params;

    @JsonProperty(value = "log_time")
    private String logTime;

    @JsonProperty(value = "server_ip")
    private String serverIp;

    @JsonProperty(value = "request_body")
    private Object requestBody;

    @JsonProperty(value = "error_code")
    private ErrorCode errorCode;

    @JsonProperty(value = "error_message")
    private String errorMessage;


    @JsonProperty(value = "user_id")
    private String userId;


    public ReqResLogging(String errorType, String httpMethod, String uri, Map<String, Object> params, String logTime, String serverIp, Object requestBody, ErrorCode errorCode, String errorMessage, String userId) {
        this.errorType = errorType;
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.params = params;
        this.logTime = logTime;
        this.serverIp = serverIp;
        this.requestBody = requestBody;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.userId = userId;
    }
}