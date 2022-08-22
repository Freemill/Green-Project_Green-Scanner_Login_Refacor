package com.garb.gbcollector.login.web;

import com.garb.gbcollector.login.domain.memberdao.MemberRepository;
import com.garb.gbcollector.login.domain.membervo.Member;
import com.garb.gbcollector.login.web.validation.form.MemberSaveForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class LoginTestDataInit {

    private final MemberRepository memberRepository;

    @PostConstruct
    public void init(){
        memberRepository.save(new Member(1, "gildong@naver.com", "gildong", "killdong", "killdong", true, true));
        memberRepository.save(new Member(2, "chulsoo@naver.com", "chulsoo", "chulsuck", "chulsuck", true, true));
    }
}
