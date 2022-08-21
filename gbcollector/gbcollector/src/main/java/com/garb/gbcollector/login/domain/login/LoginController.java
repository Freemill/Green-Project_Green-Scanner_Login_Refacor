package com.garb.gbcollector.login.domain.login;

import com.garb.gbcollector.login.web.login.LoginForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@Slf4j
public class LoginController {

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "html/login";
    }
}
