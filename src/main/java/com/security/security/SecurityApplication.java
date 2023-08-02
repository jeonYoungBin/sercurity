package com.security.security;

import com.security.security.proxy.ExchangeOpenFeign;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
//@EnableFeignClients(defaultConfiguration = {ExchangeOpenFeign.class}, basePackages = "com.security.security.proxy")
public class SecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

}
