package com.example.smstest.domain.auth;

import com.example.smstest.domain.auth.dto.AccountRequest;
import com.example.smstest.domain.auth.dto.ResetPasswordRequest;
import com.example.smstest.domain.support.dto.SupportRequest;
import com.example.smstest.domain.support.dto.SupportResponse;
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
    @GetMapping("/login")
    public String login(){
        return "login";
    }
    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("accountRequestDto",new AccountRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(AccountRequest accountRequest, BindingResult bindingResult){

        authValidator.validate(accountRequest, bindingResult);

        if(bindingResult.hasErrors()) {
            return "register"; // 실패
        }
        else {
            // 성공
            authService.save(accountRequest);
            return "redirect:/";
        }
    }

    @GetMapping("/resetPassword")
    public String resetPassword(Model model){
        model.addAttribute("ResetPasswordRequest",new ResetPasswordRequest());
        return "resetPassword";
    }
    @PostMapping("/resetPassword")
    public String resetPassword(ResetPasswordRequest resetPasswordRequest, BindingResult bindingResult) {

        authValidator.validatPassword(resetPasswordRequest, bindingResult);

        if(bindingResult.hasErrors()) {
            return "resetPassword"; // 실패
        }
        else {
            // 성공
            authService.savePassword(resetPasswordRequest);
            return "redirect:/";
        }
    }


}
