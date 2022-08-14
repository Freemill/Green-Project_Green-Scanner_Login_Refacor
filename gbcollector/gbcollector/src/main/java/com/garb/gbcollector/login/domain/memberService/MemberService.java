package com.garb.gbcollector.login.domain.memberservice;

import com.garb.gbcollector.login.domain.membervo.Member;

public interface MemberService {

    void join(Member member);

    boolean nickNameDuplicateCheck(String nickName);
}
