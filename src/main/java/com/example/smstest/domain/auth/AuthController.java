package com.example.smstest.domain.auth;

import com.example.smstest.domain.auth.Enum.Position;
import com.example.smstest.domain.auth.Enum.Rank;
import com.example.smstest.domain.auth.dto.AccountRequest;
import com.example.smstest.domain.auth.dto.ModifyUserinfoRequest;
import com.example.smstest.domain.auth.dto.ResetPasswordRequest;
import com.example.smstest.domain.auth.entity.Authority;
import com.example.smstest.domain.auth.entity.Memp;
import com.example.smstest.domain.organization.entity.Team;
import com.example.smstest.domain.organization.repository.TeamRepository;
import com.example.smstest.external.employee.Employee;
import com.example.smstest.external.employee.EmployeeRepository;
import com.example.smstest.global.exception.CustomException;
import com.example.smstest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

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
    private final EmployeeRepository employeeRepository;

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

        log.info("===MODIFY USER INFO=== " +modifyUserinfoRequest);
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
            return "redirect:/logout";
        }
    }

    @PostMapping("/organizationChange/{memberId}")
    public ResponseEntity<String> organizationChange(@PathVariable("memberId") Long memberId) {

        Optional<Memp> memp = mempRepository.findById(memberId);
        if (memp.isPresent()){
            Memp currentMemp = memp.get();

            Employee employee = employeeRepository.findByUserstatusAndUserid(1, currentMemp.getUsername());

            if (employee != null) {

                // 부서이동이 있을 때 (인사 DB와 ERM DB의 팀이 일치하지 않을 때)
                if (!currentMemp.getTeam().getName().equals(employee.getDepartment().getDeptname())){

                    currentMemp.setActive(false);
                    mempRepository.save(currentMemp);

                    // 사용자별 캘린더 색상 랜덤 지정
                    Random rand = new Random();
                    String randomColor = String.format("#%02X%02X%02X", rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));

                    // 사용자 ROLE 지정 (defualt: ROLE_USER)
                    Set<Authority> authoritiesSet = new HashSet<>();
                    authoritiesSet.add(Authority.of("ROLE_USER"));

                    // ERM DB에 해당 유저를 신규 등록한다.

                    // 인사정보 DB에서 로컬 ERM DB와 매칭되는 팀 정보 가져옴, 없을 경우 에러 반환
                    Team team = teamRepository.findByName(employee.getDepartment().getDeptname())
                            .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

                    // 신규 유저 객체 생성, 저장
                    Memp newMemp = Memp.builder()
                            .name(employee.getEmpname())
                            .rank("엔지니어") // default
                            .position("팀원") // default
                            .team(team)
                            .username(employee.getUserid())
                            .password(currentMemp.getPassword())
                            .calenderColor(randomColor)
                            .authorities(authoritiesSet)
                            .authorities(authoritiesSet)
                            .active(true)
                            .build();

                    mempRepository.save(newMemp);
                    return new ResponseEntity<>("부서 이동이 성공적으로 처리되었습니다.", HttpStatus.OK);

                }
                else {
                    return new ResponseEntity<>("부서 이동 이력이 존재하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            else {
                return new ResponseEntity<>("부서 이동 처리 중 오류가 발생했습니다. 관리자에 문의해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>("부서 이동 처리 중 오류가 발생했습니다. 관리자에 문의해주세요.", HttpStatus.INTERNAL_SERVER_ERROR);

    }

}
