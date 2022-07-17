package com.cos.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
	
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		
		CorsConfiguration config = new CorsConfiguration();
		
		// 내 서버가 응답을 할 때 JSON을 자바스크립트에서 처리할 수 있게 할지를 설정하는 것.
		// false일 경우 JAVASCRIPT에서 욫청을 할 경우 응답이 오지 않음.
		config.setAllowCredentials(true);
		
		// 모든 IP에 응갑을 허용하겠다.
		config.addAllowedOrigin("*");
		
		// 모든 header에 응답을 허용하겠다.
		config.addAllowedHeader("*");
		
		// 모든 get, post, put, delete, patch 요청을 하겠다.
		config.addAllowedMethod("*");
		
		source.registerCorsConfiguration("/api/**", config);
		
		return new CorsFilter(source);
	}

}
