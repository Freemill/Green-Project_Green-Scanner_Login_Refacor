package com.garb.gbcollector.login.domain.memberdao;

import com.garb.gbcollector.login.domain.membervo.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    void save(Member member);

    Optional<Member> findByEmail(String userEmail);

    boolean findByNickName(String nickName);

    List<Member> findAll();

    Member findById(long id);

}
