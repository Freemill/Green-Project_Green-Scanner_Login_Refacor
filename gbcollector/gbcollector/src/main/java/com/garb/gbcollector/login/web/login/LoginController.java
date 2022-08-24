package com.garb.gbcollector.login.web.login;

import com.garb.gbcollector.login.domain.login.LoginService;
import com.garb.gbcollector.login.domain.membervo.Member;
import com.garb.gbcollector.login.web.SessionConst;
import com.garb.gbcollector.login.web.session.SessionManager;
import com.garb.gbcollector.login.web.validation.form.MemberLoginForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") MemberLoginForm form) {
        return "html/login";
    }

    ;

//    @PostMapping("/login")
//    public String login(@Valid @ModelAttribute("loginForm") MemberLoginForm form, BindingResult bindingResult, HttpServletResponse response) {
//        if (bindingResult.hasErrors()) {
//            return "html/login";
//        }
//
//        Member loginMember = loginService.login(form.getUserEmail(), form.getPassword());
//
//        if (loginMember == null) {
//            log.info("errors = {}", bindingResult);
//            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
//            return "html/login";
//        }
//
//        //로그인 성공 처리 TODO
//
//        //쿠키에 시간 정보를 주지 않으면 세션 쿠키(브라우저 종료시 모두 종료)
//        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
//        response.addCookie(idCookie);
//
//        return "redirect:/";
//    }

//    @PostMapping("/login")
//    public String loginV2(@Valid @ModelAttribute("loginForm") MemberLoginForm form, BindingResult bindingResult, HttpServletResponse response) {
//        if (bindingResult.hasErrors()) {
//            return "html/login";
//        }
//
//        Member loginMember = loginService.login(form.getUserEmail(), form.getPassword());
//
//        if (loginMember == null) {
//            log.info("errors = {}", bindingResult);
//            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
//            return "html/login";
//        }
//
//        //로그인 성공 처리
//
//        //세션 관리자를 통해 세션을 생성하고, 화면 데이터 보관
//        sessionManager.createSession(loginMember, response);
//        return "redirect:/";
//    }

    @PostMapping("/login")
    public String loginV3(@Valid @ModelAttribute("loginForm") MemberLoginForm form, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "html/login";
        }

        Member loginMember = loginService.login(form.getUserEmail(), form.getPassword());

        if (loginMember == null) {
            log.info("errors = {}", bindingResult);
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "html/login";
        }

        //로그인 성공 처리
        //세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성
        HttpSession session = request.getSession();
        //세션에 로그인 회원 정보 관리
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);

        return "homeIndex";
    }

//    @PostMapping("/logout")
//    public String logout(HttpServletResponse response) {
//        expireCookie(response, "memberId");
//
//        return "redirect:/";
//    }

//    @PostMapping("/logout")
//    public String logoutV2(HttpServletRequest request) {
//        sessionManager.expire(request);
//
//        return "redirect:/";
//    }

    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
