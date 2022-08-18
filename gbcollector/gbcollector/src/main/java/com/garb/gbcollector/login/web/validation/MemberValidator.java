package com.garb.gbcollector.login.web.validation;

import com.garb.gbcollector.login.domain.memberservice.MemberServiceImpl;
import com.garb.gbcollector.login.domain.membervo.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class MemberValidator implements Validator {

    private final MemberServiceImpl memberService;

    @Override
    public boolean supports(Class<?> clazz) {
        return Member.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Member member = (Member) target;


        //검증 로직
        if (member.getNumber() == null) {
            errors.rejectValue("number", "required");
        }
        if (member.getNumber() != null && (member.getNumber() <= 1000 || member.getNumber() >= 1)) {
            errors.rejectValue("number", "range", new Object[]{0, 1000}, null);
        }
        if (!StringUtils.hasText(member.getNickName())) {
            errors.rejectValue("nickName", "required");
        }
        if (memberService.nickNameDuplicateCheck(member.getNickName())) {
            errors.rejectValue("nickName", "duplicated");
        }
        if (!StringUtils.hasText(member.getPassword())) {
            errors.rejectValue("password", "required");
        }
        if (!StringUtils.hasText(member.getPasswordConfirm())) {
            errors.rejectValue("passwordConfirm", "required");
        }
        if (!StringUtils.hasText(member.getUserEmail())) {
            errors.rejectValue("userEmail", "required");
        }
        if (member.getUserEmail() != "" && emailTypeCheck(member.getUserEmail())) {
            errors.rejectValue("userEmail", "formCheck");
        }
        if (!member.isPrivacyCheck()) {
            errors.rejectValue("privacyCheck", "required");
        }
        if (!member.isTermsCheck()) {
            errors.rejectValue("termsCheck", "required");
        }

        // password와 passwordConfirm의 값이 다를 경우 검증! -> 특정 field문제가 아닌 복합 rule 검증!
        if (member.getPassword() != null && member.getPasswordConfirm() != null) {
            String memberPassword = member.getPassword();
            String memberPasswordConfirm = member.getPasswordConfirm();
            if (memberPassword != memberPasswordConfirm) {
                errors.reject("passwordSame");
            }
        }



    }
    private boolean emailTypeCheck(String userEmail) {
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userEmail);
        return !matcher.matches();
    }
}
