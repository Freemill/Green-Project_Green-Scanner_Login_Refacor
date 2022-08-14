package com.garb.gbcollector.login.domain.memberdao;

import com.garb.gbcollector.login.domain.membervo.Member;

import java.util.List;

public interface MemberRepository {

    void save(Member member);

    boolean findByEmail(Member member);

    boolean findByNickName(String nickName);

    List<Member> findAll();

}
