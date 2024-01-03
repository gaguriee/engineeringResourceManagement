package com.example.smstest.domain.auth;

import com.example.smstest.domain.auth.dto.ResetPasswordRequest;
import com.example.smstest.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 사용자 상호작용과 관련된 Controller
 * METHOD : 로그인, 회원가입, 비밀번호 재설정, 사용자 정보 변경
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/account")
public class AuthController {

    private final AuthService authService;

    /**
     * [ 로그인 ]
     * @return 로그인 페이지
     */
    @GetMapping("/login")
    public String login(){
        return "authLogin";
    }

    /**
     * [ 비밀번호 재설정 ]
     * @param model ResetPasswordRequest -> 비밀번호 재설정 DTO 줌
     * @return 비밀번호 재설정 페이지
     */
    @GetMapping("/resetPassword")
    public String resetPassword(Model model){
        model.addAttribute("ResetPasswordRequest",new ResetPasswordRequest());
        return "authResetPassword";
    }

    /**
     * [ 비밀번호 재설정 ]
     * @param resetPasswordRequest 비밀번호 재설정 DTO 받아옴
     * @param model 실패 시 에러 메시지 전달
     * @return 메인페이지 (성공), 비밀번호재설정 페이지 (실패)
     */
    @PostMapping("/resetPassword")
    public String resetPassword(ResetPasswordRequest resetPasswordRequest, Model model) {

        if(!(resetPasswordRequest.getPassword().equals(resetPasswordRequest.getPassword_confirm()))){
            //비밀번호와 비밀번호 확인이 다르다면
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "authResetPassword"; // 실패
        }
        else {
            // 성공
            authService.savePassword(resetPasswordRequest);
            return "redirect:/";
        }
    }

    /**
     * [ 부서 이동 신청 ]
     * @param memberId 부서 변경할 유저 id
     * @return
     */
    @PostMapping("/organizationChange/{memberId}")
    public ResponseEntity<String> organizationChange(@PathVariable("memberId") Long memberId) {
        try {
            String result = authService.organizationChange(memberId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
