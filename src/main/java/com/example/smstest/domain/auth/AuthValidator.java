package com.example.smstest.domain.auth;

import com.example.smstest.domain.auth.dto.AccountRequest;
import com.example.smstest.domain.auth.dto.ResetPasswordRequest;
import com.example.smstest.domain.auth.repository.MempRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class AuthValidator implements Validator {

    @Autowired
    private MempRepository mempRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return AccountRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        AccountRequest accountRequest = (AccountRequest) obj;
        if(!((AccountRequest) obj).getPassword().equals(((AccountRequest) obj).getPassword_confirm())){
            //비밀번호와 비밀번호 확인이 다르다면
            errors.rejectValue("password", "key","비밀번호가 일치하지 않습니다.");
        }
        else if(mempRepository.findByUsername(((AccountRequest) obj).getUsername()) !=null){
            // 이름이 존재하면
            errors.rejectValue("username", "key","이미 사용자 이름이 존재합니다.");
        }
    }

    public void validatPassword(ResetPasswordRequest resetPasswordRequest, Errors errors) {
        if(!(resetPasswordRequest.getPassword().equals(resetPasswordRequest.getPassword_confirm()))){
            //비밀번호와 비밀번호 확인이 다르다면
            errors.rejectValue("password", "key","비밀번호가 일치하지 않습니다.");
        }
    }
}