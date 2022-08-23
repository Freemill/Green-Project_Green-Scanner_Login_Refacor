package com.garb.gbcollector.login.web;

import com.garb.gbcollector.login.domain.memberservice.MemberServiceImpl;
import com.garb.gbcollector.login.domain.membervo.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final MemberServiceImpl memberService;

    //@GetMapping("/")
    public String IndexPage(Model model) {
        log.info("Index Page");
        return "index";
    }

    @GetMapping("/")
    public String homeLogin(@CookieValue(name = "memberId", required = false) Long memberId, Model model) {

        if (memberId == null) {
            return "index";
        }

        //로그인
        Member loginMember = memberService.findById(memberId);
        if (loginMember == null) {
            return "index";
        }

        model.addAttribute("member", loginMember);
        log.info("model = {}", model);
        return "homeIndex";
    }

    }



