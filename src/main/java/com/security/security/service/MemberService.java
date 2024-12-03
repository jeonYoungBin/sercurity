package com.security.security.service;

import com.security.security.domain.Member;
import com.security.security.repository.MemberRepository;
import com.security.security.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParseException;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    @Value("${endpoint.url}")
    private String strapUrl;
    private final MemberRepository memberRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenUtil jwtTokenProvider;

    public Member findOne(String userId) {
        return memberRepository.findById(userId);
    }

    public Member findByNameAndRegNo(String name, String regNo) {
        return memberRepository.findByNameAndReqNo(name, regNo);
    }

    public HashMap<String, Long> findScrapUser(String token, String userName, String regNo) throws Exception {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.add("Authorization", token);

        JSONObject jsonBody = new JSONObject();
        jsonBody.append("name", userName);
        jsonBody.append("regNo", regNo);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody.toString(), httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(strapUrl, HttpMethod.POST, entity, String.class);

        //response 받은 후 데이터 파싱
        HashMap<String, Long> amountInfo = new HashMap<>();
        jsonParseDataMap(response, amountInfo);

        return amountInfo;
    }

    private static void jsonParseDataMap(ResponseEntity<String> response, HashMap<String, Long> amountInfo) throws Exception {
        try {
            JSONObject parsingJson = new JSONObject(response.getBody());
            String status = parsingJson.get("status").toString();
            if(status.equals("success")) {
                JSONObject data = parsingJson.getJSONObject("data");
                JSONObject jsonList = data.getJSONObject("jsonList");
                JSONArray salaryInfo = jsonList.getJSONArray("급여");
                for (int i = 0; i < salaryInfo.length(); i++) {
                    JSONObject obj = salaryInfo.getJSONObject(i);
                    amountInfo.put("salary", Long.parseLong(obj.getString("총지급액").replace(",", "")));
                }

                //산출세액
                amountInfo.put("calculated_tax_amount", Long.parseLong(jsonList.getString("산출세액").replace(",","")));
                //소득공제 리스트
                JSONArray income_deduction = jsonList.getJSONArray("소득공제");
                for(int i = 0; i< income_deduction.length(); i++) {
                    JSONObject obj = income_deduction.getJSONObject(i);
                    String incomeType = obj.getString("소득구분");
                    if(incomeType.equals("보험료")) {
                        amountInfo.put("premium_expenses",Long.parseLong(obj.getString("금액").replace(",","")));
                    } else if(incomeType.equals("교육비")) {
                        amountInfo.put("educational_expenses", Long.parseLong(obj.getString("금액").replace(",","")));
                    } else if(incomeType.equals("기부금")) {
                        amountInfo.put("donation_expenses", Long.parseLong(obj.getString("금액").replace(",","")));
                    } else if(incomeType.equals("의료비")) {
                        amountInfo.put("medical_expenses", Long.parseLong(obj.getString("금액").replace(",","")));
                    } else { //퇴직연금
                        amountInfo.put("retirement_pension", Long.parseLong(obj.getString("총납임금액").replace(",","")));
                    }
                }
            }
        } catch (JsonParseException e) {
            throw new Exception(e.getMessage());
        }
    }

    public HashMap<String, String> refundCalucate(Member findMember) {
        HashMap<String, String> result = new HashMap<>();

        //근로소득세액공제금액 = 산출세액 * 0.55
        double earned_amount = findMember.getCalculated_tax_amount() * 0.55;

        //퇴직연금세액공제금액 = 퇴직연금 납입금액 * 0.15 (response)
        double retirement_amount = findMember.getRetirement_pension() * 0.15;
        result.put("retirement_amount", String.format("%1$,.0f", retirement_amount));

        //보험료공제금액 = 보험료납입금액 * 12%
        double premium_amount  = findMember.getPremium_expenses() * 0.12;

        //의료비공제금액 = (의료비납입금액 - 총금여 * 3%) * 15%
        double medical_amount = (findMember.getMedical_expenses() - (findMember.getSalary() * 0.03)) * 0.15;
        if(medical_amount < 0) medical_amount = 0;

        //교육비공제금액 = 교육비납입금액 * 15%
        double educational_amount = findMember.getEducational_expenses() * 0.15;

        //기부금공제금액 = 기부금납입금액 * 15%
        double donation_amount = findMember.getDonation_expenses() * 0.15;

        //특별세액공제금액
        double special_tax_amount = premium_amount + medical_amount + educational_amount + donation_amount;

        //표준세액공제금액
        double standard_tax_amount = 0;
        if(special_tax_amount < 130000) standard_tax_amount = 130000;
        else if(special_tax_amount >= 130000) standard_tax_amount = 0;

        //결정세액 = 산출세액 - 근로소득세액공제금액 - 특별세액공제금액 - 표준세액공제금액 - 퇴직연금세액공제금액
        double determined_tax_amount = findMember.getCalculated_tax_amount() - earned_amount - special_tax_amount - standard_tax_amount - retirement_amount;
        if(determined_tax_amount < 0) determined_tax_amount = 0;
        result.put("determined_tax_amount", String.format("%1$,.0f", determined_tax_amount));

        return result;
    }

    public String login(String userId, String password) {
        Member member = memberRepository.findById(userId);
        if(!passwordEncoder.matches(password,member.getPassword())) {
            throw new UsernameNotFoundException("");
        }
        return jwtTokenProvider.createToken(userId, password);
    }
}
