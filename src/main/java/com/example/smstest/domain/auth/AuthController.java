package com.example.smstest.domain.auth;

import com.example.smstest.domain.auth.Enum.Position;
import com.example.smstest.domain.auth.Enum.Rank;
import com.example.smstest.domain.auth.dto.AccountRequest;
import com.example.smstest.domain.auth.dto.ModifyUserinfoRequest;
import com.example.smstest.domain.auth.dto.ResetPasswordRequest;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.organization.repository.TeamRepository;
import com.example.smstest.global.exception.CustomException;
import com.example.smstest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final AuthValidator authValidator;
    private final TeamRepository teamRepository;
    private final MempRepository mempRepository;

    /**
     *
     * @return 로그인 페이지
     */
    @GetMapping("/login")
    public String login(){
        return "authLogin";
    }


    /**
     *
     * @param model 직급 및 직책, 팀 data 전달 (유저가 선택 가능하도록), AccountRequest -> 회원가입 DTO 줌
     * @return 회원가입 페이지
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("positions", Position.values());
        model.addAttribute("ranks", Rank.values());
        model.addAttribute("teams", teamRepository.findAll());

        model.addAttribute("accountRequest",new AccountRequest());

        return "authRegister";
    }

    /**
     *
     * @param accountRequest 회원가입 DTO
     * @param bindingResult
     * @param model 에러 내용, 직급 및 직책, 팀 data 전달 (유저가 선택 가능하도록), AccountRequest -> 회원가입 DTO 받아옴
     * @return 메인페이지 (성공), 회원가입페이지 (실패)
     */
    @PostMapping("/register")
    public String register(AccountRequest accountRequest, BindingResult bindingResult, Model model){

        authValidator.validate(accountRequest, bindingResult);

        if(bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute("positions", Position.values());
            model.addAttribute("ranks", Rank.values());
            model.addAttribute("teams", teamRepository.findAll());
            return "authRegister"; // 실패
        }
        else {
            // 성공
            Memp memp = authService.register(accountRequest);
            log.info("NEW MEMBER : " + memp.getUsername());
            return "redirect:/";
        }
    }

    /**
     *
     * @param model ResetPasswordRequest -> 비밀번호 재설정 DTO 줌
     * @return 비밀번호 재설정 페이지
     */
    @GetMapping("/resetPassword")
    public String resetPassword(Model model){
        model.addAttribute("ResetPasswordRequest",new ResetPasswordRequest());
        return "authResetPassword";
    }

    /**
     *
     * @param resetPasswordRequest 비밀번호 재설정 DTO 받아옴
     * @param bindingResult
     * @param model 실패 시 에러 메시지 전달
     * @return 메인페이지 (성공), 비밀번호재설정 페이지 (실패)
     */
    @PostMapping("/resetPassword")
    public String resetPassword(ResetPasswordRequest resetPasswordRequest, BindingResult bindingResult, Model model) {

        authValidator.validatePassword(resetPasswordRequest, bindingResult);

        if(bindingResult.hasErrors()) {
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
     *
     * @param model 직급 및 직책, 팀 data 전달 (유저가 선택 가능하도록), ModifyUserinfoRequest -> 사용자 정보 수정 DTO 줌
     * @return 사용자 정보 수정 페이지
     */
    @GetMapping("/modifyUserinfo")
    public String modifyUserinfo(Model model){
        model.addAttribute("positions", Position.values());
        model.addAttribute("ranks", Rank.values());
        model.addAttribute("teams", teamRepository.findAll());

        ModifyUserinfoRequest modifyUserinfoRequest = new ModifyUserinfoRequest();

        Memp memp = mempRepository.findByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        modifyUserinfoRequest.setRank(memp.getRank());
        modifyUserinfoRequest.setPosition(memp.getPosition());
        modifyUserinfoRequest.setTeamId(memp.getTeam().getId());

        model.addAttribute("modifyUserinfoRequest", modifyUserinfoRequest);
        return "authModifyUserinfo";
    }

    /**
     *
     * @param modifyUserinfoRequest -> 사용자 정보 수정 DTO 받아옴
     * @param bindingResult
     * @param model -> 에러메시지, 직급 및 직책, 팀 data 전달 (유저가 선택 가능하도록)
     * @return 메인페이지 (성공), 사용자 정보 수정 페이지 (실패)
     */
    @PostMapping("/modifyUserinfo")
    public String modifyUserinfo(ModifyUserinfoRequest modifyUserinfoRequest, BindingResult bindingResult, Model model) {

        authValidator.validatePassword(modifyUserinfoRequest, bindingResult);

        if(bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute("positions", Position.values());
            model.addAttribute("ranks", Rank.values());
            model.addAttribute("teams", teamRepository.findAll());
            return "authModifyUserinfo"; // 실패
        }
        else {
            authService.saveUserInfo(modifyUserinfoRequest);
            return "redirect:/";
        }
    }


}
