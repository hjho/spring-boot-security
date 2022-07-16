package com.cos.security1.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity	// 설정하면 테이블이 생성 되어있음....
@NoArgsConstructor
public class User {
	
	@Id // primary key
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// 자동생성.
	private int id;
	
	private String username;
	
	private String password;
	
	private String email;
	
	// ROLE_USER, ROLE_ADMIN
	private String role; 
	
	private String provider;
	
	private String providerId;
	
	@CreationTimestamp
	// 자동생성.
	private Timestamp createDate;

	@Builder
	public User(String username, String password, String email, String role, String provider, String providerId,
			Timestamp createDate) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.role = role;
		this.provider = provider;
		this.providerId = providerId;
		this.createDate = createDate;
	}
	
}
