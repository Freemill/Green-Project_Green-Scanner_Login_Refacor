package com.garb.gbcollector.domain.memberrepository;

import com.garb.gbcollector.login.domain.memberdao.MemberRepository;
import com.garb.gbcollector.login.domain.memberdao.MemoryMemberRepository;
import com.garb.gbcollector.login.domain.membervo.Member;
import com.garb.gbcollector.login.web.validation.form.MemberSaveForm;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
@Slf4j
public class MemoryMemberRepositoryTest {

    MemberRepository memoryMemberRepository = new MemoryMemberRepository();

    @BeforeEach
    public void beforeEach() {
        Member memberA = new Member(3, "bill@naver.com", "동생", "asdf", "asdf", true, true);
        Member memberB = new Member(4, "jeorge@naver.com", "버버리빌", "asdf", "asdf", true, true);
        Member memberC = new Member(5, "benn@naver.com", "뚱땡이", "asdf", "asdf", true, true);
        memoryMemberRepository.save(memberA);
        memoryMemberRepository.save(memberB);
        memoryMemberRepository.save(memberC);
    }

    @Test
    @DisplayName("findByNickName Test!")
    void findByNickName() {
        //given
        MemberSaveForm memberTest = new MemberSaveForm(3, "bill@naver.com", "동생", "asdf", "asdf", true, true);

        //when
        boolean nickNameCheck1 = memoryMemberRepository.findByNickName("빌덴브로");

        //then
        Assertions.assertThat(nickNameCheck1).isFalse();
    }
}


