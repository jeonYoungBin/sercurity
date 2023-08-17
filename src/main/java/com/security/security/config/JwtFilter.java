package com.security.security.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.security.domain.response.ResponseDto;
import com.security.security.utils.JwtTokenUtil;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean validateToken = true;
        ResponseDto responseEntity = new ResponseDto(501, null, null);
        if(request.getRequestURI().contains("/swagger") || request.getRequestURI().contains("api-docs")
        || request.getRequestURI().contains("/szs/login") || request.getRequestURI().contains("/szs/signup")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authorization = request.getHeader(AUTHORIZATION_HEADER);

        //토큰 유무 체크
        if(authorization == null || !authorization.startsWith("Bearer ")) {
            log.error("토큰이 없거나 잘못된 값입니다.");
            responseEntity = new ResponseDto(501, "토큰이 없거나 잘못된 값입니다.", null);
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getOutputStream().write(responseBytes(responseEntity));
            return;
        }
        try {
            //토큰 꺼네기
            String token = authorization.split(" ")[1];
            String userId = jwtTokenUtil.getMemberInfo(token);

            //권한 부여
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(userId, null, List.of(new SimpleGrantedAuthority("USER")));

            //Detail
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (UnsupportedJwtException e) {
            validateToken = false;
            log.error("[UnsupportedJwtException] "+e.getMessage());
            responseEntity = new ResponseDto(501, e.getMessage(), null);
        } catch (ExpiredJwtException e) {
            validateToken = false;
            log.error("[ExpiredJwtException] "+e.getMessage());
            responseEntity = new ResponseDto(501, e.getMessage(), null);
        } catch (MalformedJwtException e) {
            validateToken = false;
            log.error("[MalformedJwtException] "+e.getMessage());
            responseEntity = new ResponseDto(501, e.getMessage(), null);
        } catch (JwtException e) {
            validateToken = false;
            log.error("[JwtException] "+e.getMessage());
            responseEntity = new ResponseDto(501, e.getMessage(), null);
        } catch (Exception e) {
            validateToken = false;
            log.error("[Exception] "+e.getMessage());
            responseEntity = new ResponseDto(501, e.getMessage(), null);
        } finally {
            if(!validateToken) {
                response.setHeader("Content-Type", "application/json;charset=UTF-8");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getOutputStream().write(responseBytes(responseEntity));
                return;
            }
            filterChain.doFilter(request, response);
        }
    }

    private byte[] responseBytes(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String serialized = objectMapper.writeValueAsString(obj);
        return serialized.getBytes(StandardCharsets.UTF_8);
    }
}
