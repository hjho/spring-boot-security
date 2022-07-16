package com.cos.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;

@Configuration
@EnableWebSecurity // 활성화: 스프링 시큐리티 필터가 스프링 필터체인에 등록이 된다!!
@EnableGlobalMethodSecurity(securedEnabled = true, // @Secured 어노테이션 활성화
							prePostEnabled = true) // @PreAuthorize, @PostAuthorize 어노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
	// 해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다.
	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.csrf().disable();
		
		http.authorizeRequests()
			// 로그인한 사람. > 인증만 되면 들어갈 수 있는 주소.
			.antMatchers("/user/**").authenticated()
			// ADMIN, MANAGER 권한이 있는 사람. > /manager
			.antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
			// ADMIN 권한이 있는 사람. > /admin
			// 로그인을 안하면 403,, 접근 권한이 없음.
			.antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
			// 다른 요청은 다 접근 허용.
			.anyRequest().permitAll()

			// 권한이 없으면 로그인 페이지로!(403 대신)
			.and()
			// 기본적인 로그인 설정.
			.formLogin()
			.loginPage("/loginForm")
			// input name 변경 시
			//.usernameParameter("username2")
			.loginProcessingUrl("/login") 		// "/login" 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 진행해줍니다.
			// "/loginForm"으로 들어와서 로그인하면 "/"로 이동하는데
			// "/user"의 권한이 없어서 "/loginForm"으로 이동 후 로그인을 하면 "/user"로 이동해줌.
			.defaultSuccessUrl("/")		  		// main page
			
			.and()
			// OAuth 로그인 설정.
			.oauth2Login()
			.loginPage("/loginForm")
			// 구글 로그인이 완료된 뒤의 후처리가 필요함.
			// 1. 코드받기(인증)
			// 2. AccessToken을 받기(권한) > 사용자가 Google의 접근할 수 있는 권한을 받음.
			// 3. 사용자 프로필 정보를 가져옴.
			// 4-1. 그 정보를 토대로 회원가입을 자동으로 진행시키기도 함.
			// 4-2. 그 정보가 모자르다면 추가정보를 받고 회원가입 시키기도 함.
			// Tip: 구글로그인이 완료되면 -> 코드X, (엑세스토큰+사용자프로필정보O)
			.userInfoEndpoint()
			.userService(principalOauth2UserService)
			;
	}
	
	
}
