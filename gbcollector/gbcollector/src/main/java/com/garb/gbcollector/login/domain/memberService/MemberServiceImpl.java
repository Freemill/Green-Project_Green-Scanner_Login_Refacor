package com.garb.gbcollector.login.domain.memberservice;

import com.garb.gbcollector.login.domain.memberdao.MemberRepository;
import com.garb.gbcollector.login.domain.membervo.MemberSaveForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

    @Override
    public void join(MemberSaveForm member) {
        memberRepository.save(member);
    }

    @Override
    public boolean nickNameDuplicateCheck(String nickName) {
        return memberRepository.findByNickName(nickName);
    }

}
