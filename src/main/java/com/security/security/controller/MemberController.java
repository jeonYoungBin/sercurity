package com.security.security.controller;

import com.security.security.domain.Member;
import com.security.security.domain.MemberRefund;
import com.security.security.domain.response.ResponseDto;
import com.security.security.repository.MemberRepository;
import com.security.security.utils.AesService;
import com.security.security.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping("/szs")
@AllArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final AesService aesService;
    private final MemberRepository memberRepository;

    /**
     * 토큰을 이용하여 userId 파싱후 자신의 정보 리턴
     */
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "가입 정보 확인", description = "가입 정보 확인 api입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "206", description = "기타 api 오류", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public ResponseEntity<FindMemberResponse> findMember(HttpServletRequest request, Authentication authentication) {
        return ResponseEntity.ok(MemberResponseMapper.INSTANCE.findMemberResponse(memberService.findAlreadyJoinMember(authentication.getName())));
    }

    /**
     * 스트랩 정보 받아 온 후 업데이트
     */
    @PostMapping(value = "/scrap", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "가입 유저 스크랩 확인", description = "가입 유저 스크랩 확인 api입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "206", description = "기타 api 오류", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public ResponseDto userScrap(HttpServletRequest request, Authentication authentication) throws Exception {
        String token = request.getHeader("Authorization");
        ResponseDto response = new ResponseDto(200, "OK", null);

        String userId = authentication.getName();
        Member findMember = memberService.findOne(userId);
        String userName = findMember.getName();
        String regNo = aesService.decrypt(findMember.getRegNo()); //주민번호 복호화

        //스트랩정보 받아온다
        HashMap<String, Long> scrapUserInfo = memberService.findScrapUser(token, userName, regNo);
        if(!scrapUserInfo.isEmpty()) {
            Member member = memberRepository.updateMember(userName, scrapUserInfo);
            response.setResult(member);
        }

        return response;
    }

    @GetMapping(value = "/refund", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "가입 유저의 결정세액, 퇴직연금세액공제금 확인", description = "가입 유저의 결정세액, 퇴직연금세액공제금 확인하는 api입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "206", description = "기타 api 오류", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(implementation = Error.class)))
    })
    public ResponseEntity<RefundMemberResponse> findRefundMember(HttpServletRequest request, Authentication authentication) throws Exception {
        return ResponseEntity.ok(MemberResponseMapper.INSTANCE.refundMemberResponse(memberService.findRefundMember(authentication.getName())));
    }

    @Getter
    static class FindMemberResponse {
        private Long id;
        private String userId;
        private String name;
        private String regNo;

        public FindMemberResponse(Long id, String userId, String name, String regNo) {
            this.id = id;
            this.userId = userId;
            this.name = name;
            this.regNo = regNo;
        }
    }

    @Getter
    static class RefundMemberResponse {
        private Long retirementAmount;
        private Long determinedTaxAmount;
        private String name;

        public RefundMemberResponse(Long retirementAmount, Long determinedTaxAmount, String name) {
            this.retirementAmount = retirementAmount;
            this.determinedTaxAmount = determinedTaxAmount;
            this.name = name;
        }
    }

    @Mapper
    interface MemberResponseMapper {
        MemberResponseMapper INSTANCE = Mappers.getMapper(MemberResponseMapper.class);

        RefundMemberResponse refundMemberResponse(MemberRefund memberRefund);

        FindMemberResponse findMemberResponse(Member member);
    }
}
