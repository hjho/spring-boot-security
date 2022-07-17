package com.cos.jwt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cos.jwt.model.User;
import com.cos.jwt.repository.UserRepository;

// 인증이 필요한 요청은 다 거부됨.
// 인증이 필요하지 않은 요청만 허용.
// 로그인을 해야하지만 되는 요청은 해결이 안됨.
// @CrossOrigin

@RestController
public class RestApiController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("home")
	public String home() {
		return "<h1>home</h1>";
	}
	
	@PostMapping("token")
	public String token() {
		return "<h1>token</h1>";
	}
	
	@PostMapping("join")
	public String join(@RequestBody User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setRoles("ROLE_USER");
		
		System.out.println(user);
		userRepository.save(user);
		
		return "회원가입 완료";
	}
	
	// USER, MANAGER, ADMIN 권한만 접근.
	@GetMapping("/api/v1/user/{path}") 
	public String user(@PathVariable String path) {
		return "user:" + path;
	}
	// MANAGER, ADMIN 권한만 접근.
	@GetMapping("/api/v1/manager/{path}") 
	public String manager(@PathVariable String path) {
		return "manager:" + path;
	}
	// ADMIN 권한만 접근.
	@GetMapping("/api/v1/admin/{path}") 
	public String admin(@PathVariable String path) {
		return "admin:" + path;
	}
	
}
