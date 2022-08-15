//package com.garb.gbcollector.login.web.validation;
//
//import com.garb.gbcollector.login.domain.memberservice.MemberService;
//import com.garb.gbcollector.login.domain.memberservice.MemberServiceImpl;
//import com.garb.gbcollector.login.domain.membervo.Member;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Controller
//@RequiredArgsConstructor
//@Slf4j
//public class ValidationMemberControllerV1 {
//
//    private final MemberServiceImpl memberService;
//
//    @GetMapping("/memberInsertForm")
//    public String memberInsert(Model model) {
//        log.info("memberInsertForm IN");
//        model.addAttribute("member", new Member());
//        return "html/memberInsertForm";
//    }
//
//    @PostMapping("/memberInsert")
//    public String memberInsert(@ModelAttribute Member member, Model model, RedirectAttributes redirectAttributes) {
//        //검증 오류 저장소
//        Map<String, String> errors = new HashMap<>();
//
//        //검증 로직
//        if (member.getNumber() == null) {
//            errors.put("number", "번호 입력은 필수입니다.");
//        }
//        if (!StringUtils.hasText(member.getNickName())) {
//            errors.put("nickName", "닉네임 입력은 필수입니다.");
//        }
//        if (memberService.nickNameDuplicateCheck(member.getNickName())) {
//            errors.put("nickname", "중복된 닉네임 입니다.");
//        }
//        if (!StringUtils.hasText(member.getPassword())) {
//            errors.put("password", "비밀번호 입력은 필수입니다.");
//        }
//        if (!StringUtils.hasText(member.getPasswordConfirm())) {
//            errors.put("passwordConfirm", "비밀번호 확인 입력은 필수입니다.");
//        }
//        if (!StringUtils.hasText(member.getUserEmail())) {
//            errors.put("userEmail", "이메일 입력은 필수입니다.");
//        }
//        if (emailTypeCheck(member.getUserEmail())) {
//            errors.put("userEmail", "이메일 형식에 맞춰 입력해주세요.");
//        }
//        if (!member.isPrivacyCheck()) {
//            errors.put("privacyCheck", "개인정보처리방침 동의는 필수입니다.");
//        }
//        if (!member.isTermsCheck()) {
//            errors.put("termsCheck", "이용약관 동의는 필수입니다.");
//        }
//
//        // password와 passwordConfirm의 값이 다를 경우 검증! -> 특정 field문제가 아닌 복합 rule 검증!
//        if (member.getPassword() != null && member.getPasswordConfirm() != null) {
//            String memberPassword = member.getPassword();
//            String memberPasswordConfirm = member.getPasswordConfirm();
//            if (memberPassword != memberPasswordConfirm) {
//                errors.put("globalError", "비밀번호와 비밀번호확인의 입력값을 같은 값이어야 합니다.");
//            }
//        }
//
//        //검증에 실패하면 다시 입력 폼으로!
//        if (!errors.isEmpty()) {
//            log.info("errors = {}", errors);
//            model.addAttribute("errors", errors);
//            return "html/memberInsertForm";
//        }
//
//        // 성공 로직
//        memberService.join(member);
//
//        return "redirect:/";
//    }
//
//    private boolean emailTypeCheck(String userEmail) {
//        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(userEmail);
//        return !matcher.matches();
//    }
//}
