package com.garb.gbcollector.login.web;

import com.garb.gbcollector.login.domain.membervo.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HomeController {

    @GetMapping("/")
    public String IndexPage(Model model) {
        log.info("Index Page");
        return "index";
    }
    }



