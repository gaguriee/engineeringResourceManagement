package com.example.smstest.domain.auth;

import com.example.smstest.domain.auth.Enum.Position;
import com.example.smstest.domain.auth.Enum.Rank;
import com.example.smstest.domain.auth.dto.AccountRequest;
import com.example.smstest.domain.auth.dto.ModifyUserinfoRequest;
import com.example.smstest.domain.auth.dto.ResetPasswordRequest;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.auth.repository.MempRepository;
import com.example.smstest.domain.team.repository.TeamRepository;
import com.example.smstest.exception.CustomException;
import com.example.smstest.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/account")
public class AuthController {

    private final AuthService authService;
    private final AuthValidator authValidator;
    private final TeamRepository teamRepository;
    private final MempRepository mempRepository;

    @GetMapping("/login")
    public String login(){
        return "login";
    }



    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("positions", Position.values());
        model.addAttribute("ranks", Rank.values());
        model.addAttribute("teams", teamRepository.findAll());

        model.addAttribute("accountRequest",new AccountRequest());

        return "register";
    }

    @PostMapping("/register")
    public String register(AccountRequest accountRequest, BindingResult bindingResult, Model model){

        authValidator.validate(accountRequest, bindingResult);

        if(bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute("positions", Position.values());
            model.addAttribute("ranks", Rank.values());
            model.addAttribute("teams", teamRepository.findAll());
            return "register"; // 실패
        }
        else {
            // 성공
            Memp memp = authService.register(accountRequest);
            log.info("NEW MEMBER : " + memp.getUsername());
            return "redirect:/";
        }
    }
    @GetMapping("/resetPassword")
    public String resetPassword(Model model){
        model.addAttribute("ResetPasswordRequest",new ResetPasswordRequest());
        return "resetPassword";
    }
    @PostMapping("/resetPassword")
    public String resetPassword(ResetPasswordRequest resetPasswordRequest, BindingResult bindingResult, Model model) {

        authValidator.validatePassword(resetPasswordRequest, bindingResult);

        if(bindingResult.hasErrors()) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "resetPassword"; // 실패
        }
        else {
            // 성공
            authService.savePassword(resetPasswordRequest);
            return "redirect:/";
        }
    }

    @GetMapping("/modifyUserinfo")
    public String modifyUserinfo(Model model){
        model.addAttribute("positions", Position.values());
        model.addAttribute("ranks", Rank.values());
        model.addAttribute("teams", teamRepository.findAll());

        ModifyUserinfoRequest modifyUserinfoRequest = new ModifyUserinfoRequest();

        Memp memp = mempRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        modifyUserinfoRequest.setRank(memp.getRank());
        modifyUserinfoRequest.setPosition(memp.getPosition());
        modifyUserinfoRequest.setTeamId(memp.getTeam().getId());

        model.addAttribute("modifyUserinfoRequest", modifyUserinfoRequest);
        return "modifyUserinfo";
    }

    @PostMapping("/modifyUserinfo")
    public String modifyUserinfo(ModifyUserinfoRequest modifyUserinfoRequest, BindingResult bindingResult, Model model) {

        authValidator.validatePassword(modifyUserinfoRequest, bindingResult);

        if(bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            model.addAttribute("positions", Position.values());
            model.addAttribute("ranks", Rank.values());
            model.addAttribute("teams", teamRepository.findAll());
            return "modifyUserinfo"; // 실패
        }
        else {
            authService.saveUserInfo(modifyUserinfoRequest);
            return "redirect:/";
        }
    }


}
