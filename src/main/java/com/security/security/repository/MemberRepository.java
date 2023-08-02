package com.security.security.repository;

import com.security.security.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberRepository {
    private final EntityManager em;

    public Member findById(String userId) {
        try {
            return em.createQuery("select m from Member m where m.userId= :userId", Member.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new NoResultException("존재하지 않는 회원입니다.");
        }
    }

    public Member findByNameAndReqNo(String name, String regNo) {
        try {
            return em.createQuery("select m from Member m where m.name= :name and m.regNo = :regNo", Member.class)
                    .setParameter("name", name)
                    .setParameter("regNo", regNo)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new NoResultException("존재하지 않는 회원입니다.");
        }
    }

    @Transactional
    public Member updateMember(String name, String regNo, HashMap<String, Long> scrapUserInfo) {
        Member findMember = em.createQuery("select m from Member m where m.name= :name", Member.class)
                .setParameter("name", name)
                .getSingleResult();
        //산출세액
        findMember.setCalculated_tax_amount(scrapUserInfo.get("calculated_tax_amount").longValue());
        //기부금
        findMember.setDonation_expenses(scrapUserInfo.get("donation_expenses").longValue());
        //교육비
        findMember.setEducational_expenses(scrapUserInfo.get("educational_expenses").longValue());
        //보험료
        findMember.setPremium_expenses(scrapUserInfo.get("premium_expenses").longValue());
        //의료비
        findMember.setMedical_expenses(scrapUserInfo.get("medical_expenses").longValue());
        //퇴직연금
        findMember.setRetirement_pension(scrapUserInfo.get("retirement_pension").longValue());
        //급여
        findMember.setSalary(scrapUserInfo.get("salary").longValue());

        return findMember;
    }

}
