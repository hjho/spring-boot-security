package com.cos.jwt.filter;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyFilter3 implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("필터3");
		
		HttpServletRequest  req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		/**
		 * 토큰: "hjho" 이걸 만들어줘야함.
		 * ID, PW가 정상적으로 들어와서 로그인이 안료되면 토큰을 만들어주고 그걸 응답을 해준다.
		 * 
		 * 요청 할 때마다 header에 Authorization에 value값으로 토큰을 가지고 옴.
		 * 그 때 토큰이 넘어오면 이 토큰이 내가 만든 토큰이 맞는지만 검증만 하면 됨.(RSA, HS256) >> 전자서명. 
		 */
		if("POST".equals(req.getMethod())) {
			String headerAuth = req.getHeader("Authorization");
			System.out.println(headerAuth);
			
			if("hjho".equals(headerAuth)) {
				// 게속 프로세스를 진행!
				chain.doFilter(req, res);
				
			} else {
				// Controller를 못들어오게.
				PrintWriter out = res.getWriter();
				out.print("인증안됨");
			}
		}
	}

}
