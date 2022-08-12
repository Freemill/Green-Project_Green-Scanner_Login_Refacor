package com.garb.gbcollector.login.domain.memberdao;

import com.garb.gbcollector.login.domain.membervo.Member;

public interface MemberRepository {

    void save(Member member);

}
