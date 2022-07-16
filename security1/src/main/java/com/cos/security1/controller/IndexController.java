package com.cos.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Controller
public class IndexController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/test/login")
	public @ResponseBody String testLogin(Authentication authentication,
										  @AuthenticationPrincipal PrincipalDetails principalDetails) {	// DI(의존성주입)
		System.out.println("================== /test/login");
		
		// UserDetails authPrincipal = (UserDetails) authentication.getPrincipal();
		PrincipalDetails authPrincipal = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("authentication: " + authPrincipal.getUser());
		
		System.out.println("userDetails: " + principalDetails.getUser());
		return "세션 정보 확인하기";
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String testOAuthLogin(Authentication authentication,
											   @AuthenticationPrincipal OAuth2User oauth2User) {	// DI(의존성주입)
		System.out.println("================== /test/oauth/login");
		
		OAuth2User oauthPrincipal = (OAuth2User) authentication.getPrincipal();
		System.out.println("authentication: " + oauthPrincipal.getAttributes());
		
		System.out.println("oauth2User: " + oauth2User.getAttributes());
		return "OAuth 세션 정보 확인하기";
	}
	
	// 로그인O
	@GetMapping({"", "/"})
	public String index() {
		return "index"; // 머스테치
	}
	
	// 로그인O
	// (testLogin) 일반 로그인을 해도 PrincipalDetails
	// (testOAuthLogin) OAuth 로그인을 해도 PrincipalDetails
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails: " + principalDetails.getUser());
		return "user";
	}
	
	// 로그인O 어드민 권한
	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}
	// 로그인O 매니저 권한
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}
	
	// 스프링 시큐리티가 해당 주소("/login")를 낚아채버리네요!! >>> SecurityConfig 파일 생성 후 작성 안함.
	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}
	
	// 회원 가입 페이지
	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}
	
	// 회원 가입 시킴
	@PostMapping("/join")
	public String join(User user) {
		user.setRole("ROLE_USER");
		String rawPassword = user.getPassword();
		
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		
		// 회원가입은 잘됨. 근데 비밀번호: 1234 => 이유는 패스워드가 암호화가 되어있지 않기 때문에.
		System.out.print("## 회원가입: ");
		System.out.println(user);
		userRepository.save(user);
		System.out.println("## 회원가입 완료");
		return "redirect:/loginForm";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인 정보";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "데이터 정보";
	}
	
}
