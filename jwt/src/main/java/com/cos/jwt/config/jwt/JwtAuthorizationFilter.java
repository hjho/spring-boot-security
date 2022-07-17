package com.cos.jwt.config.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwt.config.auth.PrincipalDetails;
import com.cos.jwt.model.User;
import com.cos.jwt.repository.UserRepository;

/**
 * <pre>
 * 시큐리티가 filter가지고있는데 그 필터 중에 BasicAuthenticationFilter 라는 것이 있음.
 * 권한이나 인증이 필요한 특정 주소를 요청했을 때 위 필터를 무조건 타게 되어있음.
 * 만약에 권한이 인증이 필요한 주소가 아니라면 이 필터를 안탄다.
 * </pre>
 * @author HeoJH
 *
 */
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
	
	private UserRepository userRepository;

	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
		super(authenticationManager);
		this.userRepository = userRepository;
	}
	
	// 인증이나 권한이 필요한 주소요청이 있을때 해당 필터를 타게 됨.
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// super.doFilterInternal(request, response, chain);
		System.out.println("인증이나 권한이 필요한 주소 요청이 됨.");
		
		String authorization = request.getHeader("Authorization");
		System.out.println("authorization: " + authorization);
		
		// header가 있는지 확인.
		if(authorization == null || !authorization.startsWith("Bearer")) {
			chain.doFilter(request, response);
			return;
		}
		// JWT 토큰을 검증을 해서 정상적인 사용자인지 확인.
		String jwt = authorization.replace("Bearer ", "");
		
		String username = 
				JWT.require(Algorithm.HMAC512("hjho"))
				.build()
				.verify(jwt)
				.getClaim("username").asString();
		
		// 서명이 정상적으로 됨.
		if(username != null) {
			User userEntity = userRepository.findByUsername(username);
			System.out.println("인증완료1: " + userEntity); 
			System.out.println("인증완료2: " + userEntity.getRoleList().toString()); 
			
			PrincipalDetails principalDetails = new PrincipalDetails(userEntity);
			System.out.println("인증완료3: " + principalDetails); 
			
			// JWT 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 만들어준다.
			Authentication authentication =
					new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
			
			System.out.println("인증완료4: " + authentication); 
			// 강제로 시큐리티의 세션에 접근하여 Authentication 객체를 저장.
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			chain.doFilter(request, response);
		}
	}

}
