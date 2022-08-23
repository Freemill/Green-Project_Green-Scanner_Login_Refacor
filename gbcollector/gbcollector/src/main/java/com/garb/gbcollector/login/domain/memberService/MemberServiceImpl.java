package com.garb.gbcollector.login.domain.memberservice;

import com.garb.gbcollector.login.domain.memberdao.MemberRepository;
import com.garb.gbcollector.login.domain.membervo.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public boolean nickNameDuplicateCheck(String nickName) {
        return memberRepository.findByNickName(nickName);
    }

    @Override
    public Member findById(long id) {
        return memberRepository.findById(id);
    }
}


