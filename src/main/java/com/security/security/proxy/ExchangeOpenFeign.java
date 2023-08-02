package com.security.security.proxy;

import org.json.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.HashMap;


@FeignClient(name = "ExchangeOpenFeign", url = "${endpoint.url}")
public interface ExchangeOpenFeign {
    @PostMapping
    Object codeTest(@RequestHeader("Authorization") String token, @RequestBody String data);
}
