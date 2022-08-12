package com.garb.gbcollector.login.web.validation;

import com.garb.gbcollector.login.domain.memberService.MemberService;
import com.garb.gbcollector.login.domain.membervo.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ValidationMemberController {

    private final MemberService memberService;

    @GetMapping("/memberInsertForm")
    public String memberInsert(Model model) {
        log.info("memberInsertForm IN");
        model.addAttribute("member", new Member());
        return "html/memberInsertForm";
    }

    @PostMapping("/memberInsert")
    public String memberInsert(@ModelAttribute Member member) {
        Long id = member.getId();
        String name = member.getName();
        String nickName = member.getNickName();
        String password = member.getPassword();
        String passwordConfirm = member.getPasswordConfirm();
        String userEmail = member.getUserEmail();
        boolean privacyCheck = member.isPrivacyCheck();
        boolean termsCheck = member.isTermsCheck();

        log.info("sdfsadfsdfsdfsdf");
        log.info("[{}][{}][{}][{}][{}][{}][{}][{}]", id, name, nickName, password, passwordConfirm, userEmail, privacyCheck, termsCheck);

        return null;
    }
}
