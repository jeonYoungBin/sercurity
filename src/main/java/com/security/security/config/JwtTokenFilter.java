//package com.security.security.config;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.security.security.domain.response.ResponseDto;
//import io.jsonwebtoken.*;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class JwtTokenFilter extends OncePerRequestFilter {
//    private final JwtTokenProvider jwtTokenProvider;
//
//    private byte[] responseBytes(Object obj) {
//        JSONObject jsonObject = new JSONObject(obj);
//        byte[] jsonByteArray = jsonObject.toString().getBytes(StandardCharsets.UTF_8);
//        return jsonByteArray;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
////        try {
////            if(token != null && jwtTokenProvider.validationToken(token)) {
////
////            }
////        } catch (Exception e) {
////            SecurityContextHolder.clearContext();
////            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
////            return;
////        }
//        Boolean validateToken = true;
//        String requestURI = request.getRequestURI();
//        if(requestURI.equals("/szs/login") || requestURI.equals("/szs/signup")
//                || requestURI.contains("/swagger-ui/index.html")) {
//            filterChain.doFilter(request,response);
//            return;
//        }
//
//        ResponseDto resData = new ResponseDto(500, "", null);
//        try {
//            String token = jwtTokenProvider.resolveToken(request);
//            if(token == null || token == "") {
//                validateToken = false;
//                resData = new ResponseDto(500, "token is null", null);
//            }
//        } catch (UnsupportedJwtException e) {
//            validateToken = false;
//            log.error(e.getMessage());
//            resData = new ResponseDto(500, e.getMessage(), null);
//        } catch (MalformedJwtException e) {
//            validateToken = false;
//            log.error(e.getMessage());
//            resData = new ResponseDto(500, e.getMessage(), null);
//        } catch (SignatureException e) {
//            validateToken = false;
//            log.error(e.getMessage());
//            resData = new ResponseDto(500, e.getMessage(), null);
//        } catch (ExpiredJwtException e) {
//            validateToken = false;
//            log.error(e.getMessage());
//            resData = new ResponseDto(500, e.getMessage(), null);
//        } catch (IllegalArgumentException e) {
//            validateToken = false;
//            log.error(e.getMessage());
//            resData = new ResponseDto(500, e.getMessage(), null);
//        } catch (Exception e) {
//            validateToken = false;
//            log.error(e.getMessage());
//            resData = new ResponseDto(500, e.getMessage(), null);
//        } finally {
//            if(!validateToken) {
//                response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                response.getOutputStream().write(responseBytes(resData));
//                response.setHeader("Content-Type", "application/json;charset=UTF-8");
//                return;
//            }
//            filterChain.doFilter(request,response);
//        }
//    }
//}
