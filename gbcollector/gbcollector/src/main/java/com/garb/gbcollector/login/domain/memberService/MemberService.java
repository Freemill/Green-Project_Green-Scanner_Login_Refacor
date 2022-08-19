package com.garb.gbcollector.login.domain.memberservice;

import com.garb.gbcollector.login.domain.membervo.MemberSaveForm;

public interface MemberService {

    void join(MemberSaveForm member);

    boolean nickNameDuplicateCheck(String nickName);
}
