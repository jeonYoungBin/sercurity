package com.security.security.repository;

import com.security.security.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthRepository {

    private final EntityManager em;
    /**
     * 디비 저장
     */
    @Transactional
    public void save(Member member) {
        em.persist(member);
    }

    /**
     * 가입 가능한 이름&주민번호 체크
     */
    public Boolean posibleJoinMember(String name, String regNo) {
        Boolean flag = false;

        List<Pair<String, String>> pairList = new ArrayList<>();
        pairList.add(Pair.of("홍길동","860824-1655068"));
        pairList.add(Pair.of("김둘리","921108-1582816"));
        pairList.add(Pair.of("마징가","880601-2455116"));
        pairList.add(Pair.of("배지터","910411-1656116"));
        pairList.add(Pair.of("손오공","820326-2715702"));

        for (Pair<String, String> pair : pairList) {
            if (pair.getFirst().equals(name) && pair.getSecond().equals(regNo)) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    /**
     * 회원아이디 중복 체크
     */
    public Boolean existById(String userId) {
        try {
            em.createQuery("select m from Member m where m.userId= :userId", Member.class)
                    .setParameter("userId", userId)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return true;
        }
        return false;
    }

}
