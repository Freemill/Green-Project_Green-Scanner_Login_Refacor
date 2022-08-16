package com.garb.gbcollector.login.web.validation;

import com.garb.gbcollector.login.domain.memberservice.MemberServiceImpl;
import com.garb.gbcollector.login.domain.membervo.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ValidationMemberControllerV2 {

    private final MemberServiceImpl memberService;

    @GetMapping("/memberInsertForm")
    public String memberInsert(Model model) {
        log.info("memberInsertForm IN");
        model.addAttribute("member", new Member());
        return "html/memberInsertForm";
    }

//    @PostMapping("/memberInsert")
//    public String memberInsertV1(@ModelAttribute Member member, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
//        log.info("userEmail = {}", member.getUserEmail());
//        //검증 로직
//        if (member.getNumber() == null) {
//            bindingResult.addError(new FieldError("member", "number","번호 입력은 필수입니다."));
//        }
//        if (!StringUtils.hasText(member.getNickName())) {
//            bindingResult.addError(new FieldError("member", "nickName", "닉네임 입력은 필수입니다."));
//        }
//        if (memberService.nickNameDuplicateCheck(member.getNickName())) {
//            bindingResult.addError(new FieldError("member", "nickName", "중복된 닉네임입니다."));
//        }
//        if (!StringUtils.hasText(member.getPassword())) {
//            bindingResult.addError(new FieldError("member", "password", "비밀번호 입력은 필수입니다."));
//        }
//        if (!StringUtils.hasText(member.getPasswordConfirm())) {
//            bindingResult.addError(new FieldError("member", "passwordConfirm", "비밀번호 확인 입력은 필수입니다."));
//        }
//        if (!StringUtils.hasText(member.getUserEmail())) {
//            bindingResult.addError(new FieldError("member", "userEmail", "이메일 입력은 필수입니다."));
//        }
//        if (member.getUserEmail() != "" && emailTypeCheck(member.getUserEmail())) {
//            bindingResult.addError(new FieldError("member", "userEmail", "이메일 형식에 맞춰 입력해주세요."));
//        }
//        if (!member.isPrivacyCheck()) {
//            bindingResult.addError(new FieldError("member", "privacyCheck", "개인정보처리방침 동의는 필수입니다."));
//        }
//        if (!member.isTermsCheck()) {
//            bindingResult.addError(new FieldError("member", "termsCheck", "이용약관 동의는 필수입니다."));
//        }

//        @PostMapping("/memberInsert")
//        public String memberInsertV2(@ModelAttribute Member member, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
//            log.info("userEmail = {}", member.getUserEmail());
//            //검증 로직
//            if (member.getNumber() == null) {
//                bindingResult.addError(new FieldError("member", "number","번호 입력은 필수입니다."));
//            }
//            if (!StringUtils.hasText(member.getNickName())) {
//                bindingResult.addError(new FieldError("member", "nickName", "닉네임 입력은 필수입니다."));
//            }
//            if (memberService.nickNameDuplicateCheck(member.getNickName())) {
//                bindingResult.addError(new FieldError("member", "nickName", member.getNickName(), false, null, null, "중복된 닉네임입니다."));
//            }
//            if (!StringUtils.hasText(member.getPassword())) {
//                bindingResult.addError(new FieldError("member", "password", "비밀번호 입력은 필수입니다."));
//            }
//            if (!StringUtils.hasText(member.getPasswordConfirm())) {
//                bindingResult.addError(new FieldError("member", "passwordConfirm", "비밀번호 확인 입력은 필수입니다."));
//            }
//            if (!StringUtils.hasText(member.getUserEmail())) {
//                bindingResult.addError(new FieldError("member", "userEmail", "이메일 입력은 필수입니다."));
//            }
//            if (member.getUserEmail() != "" && emailTypeCheck(member.getUserEmail())) {
//                bindingResult.addError(new FieldError("member", "userEmail", member.getUserEmail(), false, null, null, "이메일 형식에 맞춰 입력해주세요."));
//            }
//            if (!member.isPrivacyCheck()) {
//                bindingResult.addError(new FieldError("member", "privacyCheck", "개인정보처리방침 동의는 필수입니다."));
//            }
//            if (!member.isTermsCheck()) {
//                bindingResult.addError(new FieldError("member", "termsCheck", "이용약관 동의는 필수입니다."));
//            }
//
//        // password와 passwordConfirm의 값이 다를 경우 검증! -> 특정 field문제가 아닌 복합 rule 검증!
//        if (member.getPassword() != null && member.getPasswordConfirm() != null) {
//            String memberPassword = member.getPassword();
//            String memberPasswordConfirm = member.getPasswordConfirm();
//            if (memberPassword != memberPasswordConfirm) {
//                bindingResult.addError(new ObjectError("member", null, null, "비밀번호와 비밀번호확인의 입력값을 같은 값이어야 합니다."));
//            }
//        }
//
//        //검증에 실패하면 다시 입력 폼으로!
//        if (bindingResult.hasErrors()) {
//            log.info("errors = {}", bindingResult);
//            return "html/memberInsertForm";
//        }
//
//        // 성공 로직
//        memberService.join(member);
//
//        return "redirect:/";
//    }


//    @PostMapping("/memberInsert")
//    public String memberInsertV3(@ModelAttribute Member member, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
//        log.info("userEmail = {}", member.getUserEmail());
//        //검증 로직
//        if (member.getNumber() == null) {
//            bindingResult.addError(new FieldError("member", "number", member.getNickName(), false, new String[]{"required.member.number"}, null, null));
//        }
//        if (member.getNumber() != null && (member.getNumber() <= 1000 || member.getNumber() >= 1)) {
//            bindingResult.addError(new FieldError("member", "number", member.getNumber(), false, new String[]{"range.member.number"}, new Object[]{0, 1000}, null));
//        }
//        if (!StringUtils.hasText(member.getNickName())) {
//            bindingResult.addError(new FieldError("member", "nickName", member.getNickName(), false, new String[]{"required.member.nickName"}, null, null));
//        }
//        if (memberService.nickNameDuplicateCheck(member.getNickName())) {
//            bindingResult.addError(new FieldError("member", "nickName", member.getNickName(), false, new String[]{"duplicated.member.nickName"}, null, null));
//        }
//        if (!StringUtils.hasText(member.getPassword())) {
//            bindingResult.addError(new FieldError("member", "password", member.getPassword(), false, new String[]{"required.member.password"}, null,  null));
//        }
//        if (!StringUtils.hasText(member.getPasswordConfirm())) {
//            bindingResult.addError(new FieldError("member", "passwordConfirm", member.getPasswordConfirm(), false, new String[]{"required.member.passwordConfirm"}, null, null));
//        }
//        if (!StringUtils.hasText(member.getUserEmail())) {
//            bindingResult.addError(new FieldError("member", "userEmail", member.getUserEmail(), false, new String[]{"required.member.userEmail"}, null, null));
//        }
//        if (member.getUserEmail() != "" && emailTypeCheck(member.getUserEmail())) {
//            bindingResult.addError(new FieldError("member", "userEmail", member.getUserEmail(), false, new String[]{"formCheck.member.userEmail"}, null, null));
//        }
//        if (!member.isPrivacyCheck()) {
//            bindingResult.addError(new FieldError("member", "privacyCheck", "개인정보처리방침 동의는 필수입니다."));
//        }
//        if (!member.isTermsCheck()) {
//            bindingResult.addError(new FieldError("member", "termsCheck", "이용약관 동의는 필수입니다."));
//        }
//
//        // password와 passwordConfirm의 값이 다를 경우 검증! -> 특정 field문제가 아닌 복합 rule 검증!
//        if (member.getPassword() != null && member.getPasswordConfirm() != null) {
//            String memberPassword = member.getPassword();
//            String memberPasswordConfirm = member.getPasswordConfirm();
//            if (memberPassword != memberPasswordConfirm) {
//                bindingResult.addError(new ObjectError("member", new String[]{"passwordSame"}, null, "비밀번호와 비밀번호확인의 입력값을 같은 값이어야 합니다."));
//            }
//        }
//
//        //검증에 실패하면 다시 입력 폼으로!
//        if (bindingResult.hasErrors()) {
//            log.info("errors = {}", bindingResult);
//            return "html/memberInsertForm";
//        }
//
//        // 성공 로직
//        memberService.join(member);
//
//        return "redirect:/";
//    }

    @PostMapping("/memberInsert")
    public String memberInsertV4(@ModelAttribute Member member, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        log.info("userEmail = {}", member.getUserEmail());
        //검증 로직
        if (member.getNumber() == null) {
            bindingResult.rejectValue("number", "required",  new Object[]{0, 1000}, null);
        }
        if (member.getNumber() != null && (member.getNumber() <= 1000 || member.getNumber() >= 1)) {
            bindingResult.rejectValue("number", "range");
        }
        if (!StringUtils.hasText(member.getNickName())) {
            bindingResult.rejectValue("nickName", "required");
        }
        if (memberService.nickNameDuplicateCheck(member.getNickName())) {
            bindingResult.rejectValue("nickName", "duplicated");
        }
        if (!StringUtils.hasText(member.getPassword())) {
            bindingResult.rejectValue("password", "required");
        }
        if (!StringUtils.hasText(member.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "required");
        }
        if (!StringUtils.hasText(member.getUserEmail())) {
            bindingResult.rejectValue("userEmail", "required");
        }
        if (member.getUserEmail() != "" && emailTypeCheck(member.getUserEmail())) {
            bindingResult.rejectValue("userEmail", "formCheck");
        }
        if (!member.isPrivacyCheck()) {
            bindingResult.addError(new FieldError("member", "privacyCheck", "개인정보처리방침 동의는 필수입니다."));
        }
        if (!member.isTermsCheck()) {
            bindingResult.addError(new FieldError("member", "termsCheck", "이용약관 동의는 필수입니다."));
        }

        // password와 passwordConfirm의 값이 다를 경우 검증! -> 특정 field문제가 아닌 복합 rule 검증!
        if (member.getPassword() != null && member.getPasswordConfirm() != null) {
            String memberPassword = member.getPassword();
            String memberPasswordConfirm = member.getPasswordConfirm();
            if (memberPassword != memberPasswordConfirm) {
                bindingResult.reject("passwordSame");
            }
        }

        //검증에 실패하면 다시 입력 폼으로!
        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult);
            return "html/memberInsertForm";
        }

        // 성공 로직
        memberService.join(member);

        return "redirect:/";
    }

    private boolean emailTypeCheck(String userEmail) {
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userEmail);
        return !matcher.matches();
    }
}
