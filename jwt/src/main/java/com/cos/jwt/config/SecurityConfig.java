package com.cos.jwt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.filter.CorsFilter;

import com.cos.jwt.config.jwt.JwtAuthenticationFilter;
import com.cos.jwt.config.jwt.JwtAuthorizationFilter;
import com.cos.jwt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private final CorsFilter corsFilter;
	
	private final UserRepository userRepository;
	
	// 해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다.
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		/**
		 * 해당 필터는 시큐리티필터가 아님. Consider using addFilterBefore or addFilterAfter instead.
		 * http.addFilter(new MyFilter1());
		 * 
		 * 필터 걸 때 이렇게 걸 필요는 없음.
		 * http.addFilterBefore(new MyFilter3(), BasicAuthenticationFilter.class);	// 3 > 1 > 2
		 * http.addFilterAfter(new MyFilter3(), BasicAuthenticationFilter.class);	// 3 > 1 > 2
		 * 필터 중 가장 먼저 실행하는 필터(SecurityContextPersistenceFilter) 시작 전에!
		 */
		// http.addFilterBefore(new MyFilter3(), SecurityContextPersistenceFilter.class);
		
		/**
		 * Cross Site Request Forgery로 사이즈간 위조 요청인데, 즉 정상적인 사용자가 의도치 않은 위조요청을 보내는 것을 의미.
		 * 
		 * 1. REST API를 이용한 서버라면, SESSION 기반 인증과는 다르게 STATELESS하기 때문에 서버에 인증정보를 보관하지 않는다. 
		 * 2. REST API에서 client는 권한이 필요한 요청을 하기 위해서는 요청에 필요한 인증 정보를(OAuth2, JWT토큰 등)을 포함시켜야 한다.
		 * 3. spring security documentation에 non-browser clients 만을 위한 서비스라면 CSRF를 disable 하여도 좋다고 한다.
		 *  
		 * >>> 따라서 서버에 인증정보를 저장하지 않기 때문에 굳이 불필요한 CSRF 코드들을 작성할 필요가 없다.
		 * 
		 * 1. CSRF protection은 spring security에서 default로 설정된다. 
		 *    즉, protection을 통해 GET요청을 제외한 상태를 변화시킬 수 있는 POST, PUT, DELETE 요청으로부터 보호한다.
		 * 2. CSRF protection을 적용하였을 때, HTML에서 다음과 같은 CSRF 토큰이 포함되어야 요청을 받아들이게 됨으로써, 위조 요청을 방지하게 됩니다.
		 *    - <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		 */
		
		http.csrf().disable();
		
		http
			// 클라이언트의 세션 및 쿠키 정보를 관리할 필요가 없음. -> 세션을 사용하지 않겠다.
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		
			.and()
			
			/**
			 * 모든 요청에 필터를 탄다.
			 * @CrossOrigin(인증X)
			 * 시큐리티 필터에 등록 인증(O)
			 * 내 서버는 CORS 정책에서 벗어난. 모든 요청을 허용할꺼야.
			 */
			.addFilter(corsFilter)
			
			// fromLogin 안씀. -> JWT Server임.
			.formLogin().disable()	
			
			/**
			 * HTTP Basic 방식
			 * header={Authorization={ID="암호화가 안된 ID", PW="암호화가 안된 PW"}}
			 * ID, PW가 암호화가 되어 있는건 HTTPS
			 * 
			 * HTTP Bearer 방식
			 * header={Authorization={token="토큰"}}
			 * 노출이 되어도 ID, PW가 아니여서 그나마 괜찮.
			 * 
			 * 기본적인 HTTP 방식을 아예 안씀.
			 */
			.httpBasic().disable()	
			
			// fromLogin 사용안하기로 했지만 테스트를 위해서.
			// AuthenticationManager >> WebSecurityConfigurerAdapter 내부에 있음.
			.addFilter(new JwtAuthenticationFilter(authenticationManager()))	// "/login" 에서 토큰 발급.
			.addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository))		// "/**"
			
			// 권한 요청
			.authorizeRequests()
				// ROLE_USER
				.antMatchers("/api/v1/user/**")
				.access("hasRole('ROLE_USER') or hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
				// ROLE_MANAGER
				.antMatchers("/api/v1/manager/**")
				.access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
				// ROLE_ADMIN
				.antMatchers("/api/v1/admin/**")
				.access("hasRole('ROLE_ADMIN')")
			
			// 다른 요청
			.anyRequest()
				// 권한없이 들어갈 수 있음.
				.permitAll()
		;
	}
}
