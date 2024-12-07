package com.security.security.controller;

import com.security.security.domain.Member;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    public ResponseDto findMember(HttpServletRequest request, Authentication authentication) throws Exception {
        //String token = request.getHeader("Authorization");
        ResponseDto response = new ResponseDto(200, "OK", null);
        //jwt토큰 파싱
        //String jwtPasingUserId = jwtTokenProvider.getMemberInfo(token.replace("Bearer ", ""));
        String userId = authentication.getName();
        Member findMember = memberService.findOne(userId);
        findMember.setRegNo(aesService.decrypt(findMember.getRegNo()));
        response.setResult(findMember);
        return response;
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
    public ResponseDto userScrap(HttpServletRequest request,
                                 Authentication authentication) throws Exception {
        String token = request.getHeader("Authorization");
        ResponseDto response = new ResponseDto(200, "OK", null);

        String userId = authentication.getName();
        Member findMember = memberService.findOne(userId);
        String userName = findMember.getName();
        String regNo = aesService.decrypt(findMember.getRegNo()); //주민번호 복호화

        //스트랩정보 받아온다
        HashMap<String, Long> scrapUserInfo = memberService.findScrapUser(token, userName, regNo);
        if(!scrapUserInfo.isEmpty()) {
            Member member = memberRepository.updateMember(userName, regNo, scrapUserInfo);
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
    public ResponseDto findRefundMember(HttpServletRequest request, Authentication authentication) throws Exception {
        //jwt토큰 파싱
        //String token = request.getHeader("Authorization");
        ResponseDto response = new ResponseDto(200, "OK", null);

        //String jwtPasingUserId = jwtTokenProvider.getMemberInfo(token.replace("Bearer ", ""));
        String userId = authentication.getName();
        Member findMember = memberService.findOne(userId);
        HashMap<String, String> refundCalucate = memberService.refundCalucate(findMember);

        HashMap<String, Object> resultData = new HashMap<>();
        resultData.put("퇴직연금세액공제", refundCalucate.get("retirement_amount"));
        resultData.put("결정세액", refundCalucate.get("determined_tax_amount"));
        resultData.put("이름", findMember.getName());
        response.setResult(resultData);

        return response;
    }
}
