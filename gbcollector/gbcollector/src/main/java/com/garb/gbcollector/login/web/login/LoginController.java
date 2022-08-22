package com.garb.gbcollector.login.web.login;

import com.garb.gbcollector.login.domain.login.LoginService;
import com.garb.gbcollector.login.domain.membervo.Member;
import com.garb.gbcollector.login.web.validation.form.MemberLoginForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") MemberLoginForm form) {
        return "html/login";
    };

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") MemberLoginForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "html/login";
        }

        Member loginMember = loginService.login(form.getUserEmail(), form.getPassword());

        if (loginMember == null) {
            log.info("errors = {}", bindingResult);
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "html/login";
        }

        //로그인 성공 처리 TODO

        return "redirect:/";
    }
}
