package com.garb.gbcollector.login.web;

import com.garb.gbcollector.login.domain.memberservice.MemberServiceImpl;
import com.garb.gbcollector.login.domain.membervo.Member;
import com.garb.gbcollector.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final MemberServiceImpl memberService;
    private final SessionManager sessionManager;

    //@GetMapping("/")
    public String IndexPage(Model model) {
        log.info("Index Page");
        return "index";
    }

//    @GetMapping("/")
//    public String homeLogin(@CookieValue(name = "memberId", required = false) Long memberId, Model model) {
//
//        if (memberId == null) {
//            return "index";
//        }
//
//        //로그인
//        Member loginMember = memberService.findById(memberId);
//        if (loginMember == null) {
//            return "index";
//        }
//
//        model.addAttribute("member", loginMember);
//        log.info("model = {}", model);
//        return "homeIndex";
//    }

//    @GetMapping("/")
//    public String homeLoginV2(HttpServletRequest request, Model model) {
//
//        //세션 관리자에 저장된 정보를 조회회
//        Member member = (Member) sessionManager.getSession(request);
//        //로그인
//        if (member == null) {
//            return "index";
//        }
//
//        model.addAttribute("member", member);
//        log.info("model = {}", model);
//        return "homeIndex";
//    }


//    @GetMapping("/")
//    public String homeLoginV3(HttpServletRequest request, Model model) {
//
//        HttpSession session = request.getSession(false);
//        if (session == null) {
//            return "index";
//        }
//
//        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
//
//
//        //세션에 회원 데이터가 없으면 home
//        if (loginMember == null) {
//            return "index";
//        }
//
//        //세션이 유지되면 로그인으로 이동
//        model.addAttribute("member", loginMember);
//        log.info("model = {}", model);
//        return "homeIndex";
//    }

    @GetMapping("/")
    public String homeLoginV3Spring(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember, Model model) {


        //세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "index";
        }

        //세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        log.info("model = {}", model);
        return "homeIndex";
    }
    }



