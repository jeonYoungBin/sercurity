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
    private final MemberJpaDataRepository memberJpaDataRepository;

    private final EntityManager em;

    public Member findById(String userId) {
        return memberJpaDataRepository.findByUserId(userId).orElseThrow(() -> new NoResultException("존재하지 않는 회원입니다."));
    }

    public Member findByNameAndReqNo(String name, String regNo) {
        return memberJpaDataRepository.findByNameAndRegNo(name, regNo).orElseThrow(() -> new NoResultException("존재하지 않는 회원입니다."));
    }

    @Transactional
    public Member updateMember(String name, String regNo, HashMap<String, Long> scrapUserInfo) {
        Member findMember = memberJpaDataRepository.findByName(name).orElseThrow();

        //산출세액
        findMember.updateCalculatedTaxAmount(scrapUserInfo.get("calculated_tax_amount").longValue());
        //기부금
        findMember.updateDonationExpenses(scrapUserInfo.get("donation_expenses").longValue());
        //교육비
        findMember.updateEducationalExpenses(scrapUserInfo.get("educational_expenses").longValue());
        //보험료
        findMember.updatePremiumExpenses(scrapUserInfo.get("premium_expenses").longValue());
        //의료비
        findMember.updateMedicalExpenses(scrapUserInfo.get("medical_expenses").longValue());
        //퇴직연금
        findMember.updateRetirementPension(scrapUserInfo.get("retirement_pension").longValue());
        //급여
        findMember.updateSalary(scrapUserInfo.get("salary").longValue());

        return findMember;
    }

}
