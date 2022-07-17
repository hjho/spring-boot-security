package com.cos.jwt.config.jwt;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwt.config.auth.PrincipalDetails;
import com.cos.jwt.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

// 스프링 시큐리티에서 UsernamePasswordAuthenticationFilter 가 있음.
// "/login" 요청해서 username, password 전송하면(POST)
// UsernamePasswordAuthenticationFilter 가 동작을 함.
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	
	// "/login" 요청을 하면 로그인 시도를 위해서 실행되는 함수.
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		System.out.println("JwtAuthenticationFilter: 로그인 시도 중.");
		
		try {
			/*
			BufferedReader br = request.getReader();
			String input = null;
			while((input = br.readLine()) != null) {
				System.out.println(input);
			}*/
			// 1. username, password 받아서
			ObjectMapper om = new ObjectMapper();
			User user = om.readValue(request.getInputStream(), User.class);
			System.out.println(user);

			// 2. 정상인지 로그인 시도를 해보는 거에요.
			// authenticationManager 로 로그인 시도를 하면!
			UsernamePasswordAuthenticationToken authenticationToken =
					new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
			
			// PrincipalDetailsService 의 loadUserByUsername() 함수가 실행 됨.
			// DB에 있는 username과 password가 일치한다.
			Authentication authentication = authenticationManager.authenticate(authenticationToken);

			// 3. PrincipalDetails를 세션에 담고 (권한 관리를 위해서)
			// authentication 객체가 session 영역에 저장됨. >> 로그인이 되었다는 뜻.
			PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
			System.out.println("USER NAME: " + principalDetails.getUser().getUsername());
			
			// 리턴의 이유는 권한 관리가 security가 대신 해주기 대문에 편하려고 하는 거임.
			// 굳이 JWT 토큰을 사용하면서 세션을 만들 이유가 없음.
			// 근데 단지 권한 처리 때문에 session에 넣어 줍니다.
			return authentication;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	// attemptAuthentication 실행 후 인증이 전상적으로 되었으면 successfulAuthentication 함수가 실행되요.
	// JWT 토큰을 만들어서 reqeust 요청한 사용자에게 JWT토큰을 response 해주면됨.
	// 4. JWT토큰을 만들어서 응답해주면 됨.
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		System.out.println("successfulAuthentication 실행: 인증이 완료되었다는 뜻.");
		
		PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
		
		// RSA 방식은 아니고, Hash암호화방식
		String token = JWT.create()
				.withSubject("hjhoToken")
				.withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 10)))
				.withClaim("id", principalDetails.getUser().getId())
				.withClaim("username", principalDetails.getUser().getUsername())
				.sign(Algorithm.HMAC512("hjho"));
		
		// super.successfulAuthentication(request, response, chain, authResult);
		// eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9
		// .eyJzdWIiOiJoamhvVG9rZW4iLCJpZCI6MSwiZXhwIjoxNjU4MDQ4MDM0LCJ1c2VybmFtZSI6ImhqaG8ifQ.0Fig2YOJ6Vh-blHHOkaouAeoR2v_xX0Ya6S70Wml2Ol-sn538a5bySBX_UgqyL2T0hQeK-cbaOYl8zZbcJYfkQ
		response.addHeader("Authentication", "Bearer " + token);
	}
	
}
