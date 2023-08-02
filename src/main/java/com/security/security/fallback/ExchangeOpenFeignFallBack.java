package com.security.security.fallback;

import com.security.security.domain.response.ResponseDto;
import com.security.security.proxy.ExchangeOpenFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
public class ExchangeOpenFeignFallBack {
    ResponseDto exchangeOpenFeignFallback(Throwable e) {
        log.error(e.getMessage());
        return new ResponseDto(500, e.getMessage(), null);
    }
}
