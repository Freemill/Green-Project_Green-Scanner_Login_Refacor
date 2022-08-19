package com.garb.gbcollector.login.domain.memberdao;

import com.garb.gbcollector.login.domain.membervo.MemberSaveForm;

import java.util.List;

public interface MemberRepository {

    void save(MemberSaveForm member);

    boolean findByEmail(MemberSaveForm member);

    boolean findByNickName(String nickName);

    List<MemberSaveForm> findAll();

}
