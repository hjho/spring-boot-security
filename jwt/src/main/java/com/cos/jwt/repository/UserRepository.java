package com.cos.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.jwt.model.User;

// CRUD 함수를 JpaRepository가 들고 있음.
// @Respository라는 어노테이션이 없어도 IoC되요. JpaRespository를 상속했기 때문에...
public interface UserRepository extends JpaRepository<User, Long> {

	// findBy규칙 => UserName문법
	// select * from user where username = 1?
	public User findByUsername(String username); // 검색: Jpa Query Method
	
}
