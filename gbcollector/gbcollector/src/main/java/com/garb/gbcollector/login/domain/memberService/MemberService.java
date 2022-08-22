package com.garb.gbcollector.login.domain.memberservice;

import com.garb.gbcollector.login.domain.membervo.Member;
import com.garb.gbcollector.login.web.validation.form.MemberSaveForm;

public interface MemberService {

    void join(Member member);

    boolean nickNameDuplicateCheck(String nickName);
}
