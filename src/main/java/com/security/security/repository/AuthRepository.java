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
