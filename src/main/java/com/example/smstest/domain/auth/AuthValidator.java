package com.example.smstest.domain.auth;

import com.example.smstest.domain.auth.dto.AccountRequest;
import com.example.smstest.domain.auth.dto.ModifyUserinfoRequest;
import com.example.smstest.domain.auth.dto.ResetPasswordRequest;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.auth.repository.MempRepository;
import com.example.smstest.exception.CustomException;
import com.example.smstest.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * 사용자 정보 유효성 검증 클래스
 */
@Component
@RequiredArgsConstructor
public class AuthValidator implements Validator {

    private final MempRepository mempRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Validator가 AccountRequest를 지원하는지 확인
     * @param clazz the {@link Class} that this {@link Validator} is
     * being asked if it can {@link #validate(Object, Errors) validate}
     * @return
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return AccountRequest.class.equals(clazz);
    }

    /**
     * 실제 유효성 검사 로직 구현
     * @param obj the object that is to be validated
     * @param errors contextual state about the validation process
     */
    @Override
    public void validate(Object obj, Errors errors) {
        AccountRequest accountRequest = (AccountRequest) obj;
        if(!((AccountRequest) obj).getPassword().equals(((AccountRequest) obj).getPassword_confirm())){
            //비밀번호와 비밀번호 확인이 다르다면
            errors.rejectValue("password", "key","비밀번호가 일치하지 않습니다.");
        }
        else if(mempRepository.findByUsernameAndActiveTrue(((AccountRequest) obj).getUsername()) !=null){
            // 이름이 존재하면
            errors.rejectValue("username", "key","이미 사용자 이름이 존재합니다.");
        }
    }

    /**
     * 비밀번호 재설정 시 검증
     * @param resetPasswordRequest
     * @param errors
     */
    public void validatePassword(ResetPasswordRequest resetPasswordRequest, Errors errors) {
        if(!(resetPasswordRequest.getPassword().equals(resetPasswordRequest.getPassword_confirm()))){
            //비밀번호와 비밀번호 확인이 다르다면
            errors.rejectValue("password", "key","비밀번호가 일치하지 않습니다.");
        }
    }

    /**
     * 사용자 정보 수정 시 검증
     * @param modifyUserinfoRequest
     * @param errors
     */
    public void validatePassword(ModifyUserinfoRequest modifyUserinfoRequest, Errors errors) {

        Memp user = mempRepository.findByUsernameAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!passwordEncoder.matches(modifyUserinfoRequest.getPassword_confirm(), user.getPassword())){
            errors.rejectValue("password_confirm", "key","비밀번호가 일치하지 않습니다.");
        }
    }
}