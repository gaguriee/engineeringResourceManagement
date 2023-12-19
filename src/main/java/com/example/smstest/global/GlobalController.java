package com.example.smstest.global;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 전체 패키지에서 사용될 수 있는 Controller입니다.
 */
@Controller
@RequiredArgsConstructor
public class GlobalController {

    /**
     * 세션 유효성 체크하는 메소드
     * @return 
     */
    @GetMapping("/check-session")
    public ResponseEntity<String> checkSession() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
