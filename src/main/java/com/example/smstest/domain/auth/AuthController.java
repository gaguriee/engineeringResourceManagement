package com.example.smstest.domain.auth;

import com.example.smstest.domain.auth.Enum.Position;
import com.example.smstest.domain.auth.Enum.Rank;
import com.example.smstest.domain.auth.dto.AccountRequest;
import com.example.smstest.domain.auth.dto.ResetPasswordRequest;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.support.dto.SupportRequest;
import com.example.smstest.domain.support.dto.SupportResponse;
import com.example.smstest.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class AuthController {

    private final AuthService authService;
    private final AuthValidator authValidator;
    private final TeamRepository teamRepository;

    @GetMapping("/login")
    public String login(){
        return "login";
    }



    @GetMapping("/register")
    public String register(Model model) {
        // 직책과 직급 Enum 값을 전달하여 폼에서 선택할 수 있도록 합니다.
        model.addAttribute("positions", Position.values());
        model.addAttribute("ranks", Rank.values());
        model.addAttribute("teams", teamRepository.findAll());

        // Memp 객체를 생성하여 폼에 전달합니다.
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
            authService.register(accountRequest);
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

        authValidator.validatPassword(resetPasswordRequest, bindingResult);

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


}
