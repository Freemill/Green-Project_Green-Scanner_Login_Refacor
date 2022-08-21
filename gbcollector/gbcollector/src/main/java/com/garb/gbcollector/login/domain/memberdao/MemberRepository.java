package com.garb.gbcollector.login.domain.memberdao;

import com.garb.gbcollector.login.domain.membervo.MemberSaveForm;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    void save(MemberSaveForm member);

    Optional<MemberSaveForm> findByEmail(MemberSaveForm member);

    boolean findByNickName(String nickName);

    List<MemberSaveForm> findAll();

}
