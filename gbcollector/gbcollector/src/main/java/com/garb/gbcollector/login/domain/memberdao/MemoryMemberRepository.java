package com.garb.gbcollector.login.domain.memberdao;

import com.garb.gbcollector.login.domain.membervo.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class MemoryMemberRepository implements MemberRepository {

    private static Map<Long, Member> memberStore = new ConcurrentHashMap<>();
    private static Long sequence = 0L;

    @Override
    public void save(Member member) {
        member.setId(++sequence);
        log.info("member = {}", member);
        memberStore.put(member.getId(), member);
    }

    @Override
    public boolean findByEmail(Member member) {
        return false;
    }

    @Override
    public boolean findByNickName(String nickName) {
        return !findAll().stream()
                .filter(m -> m.getNickName().equals(nickName))
                .findFirst().isEmpty();
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(memberStore.values());
    }
}
