package com.garb.gbcollector.login.domain.login;

import com.garb.gbcollector.login.domain.memberdao.MemberRepository;
import com.garb.gbcollector.login.domain.membervo.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    /**
     * @return null 로그인 실패
     */
    public Member login(String userEmail, String password) {
        return memberRepository.findByEmail(userEmail)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }

}
