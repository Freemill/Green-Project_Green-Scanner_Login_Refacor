package com.garb.gbcollector.login.domain.memberdao;

import com.garb.gbcollector.login.domain.membervo.MemberSaveForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class MemoryMemberRepository implements MemberRepository {

    private static Map<Long, MemberSaveForm> memberStore = new ConcurrentHashMap<>();
    private static Long sequence = 0L;

    @Override
    public void save(MemberSaveForm member) {
        member.setId(++sequence);
        log.info("member = {}", member);
        memberStore.put(member.getId(), member);
    }

    @Override
    public boolean findByEmail(MemberSaveForm member) {
        return false;
    }

    @Override
    public boolean findByNickName(String nickName) {
        return !findAll().stream()
                .filter(m -> m.getNickName().equals(nickName))
                .findFirst().isEmpty();
    }

    @Override
    public List<MemberSaveForm> findAll() {
        return new ArrayList<>(memberStore.values());
    }
}
