# spring-boot-security
1. MySql 계정 생성.
    - create user 'id'@'%' identified by 'pw';
    - grant all privileges on *.* to 'id'@'%';
    - create database security;
    - use security;

2. Spring Boot Project
    - Spring Boot DevTools
    - // Lombok // init error
    - Spring Data JPA
    - MySQL Driver
    - Spring Security
    - Mustache
    - Spring Web

3. Spring boot init Error
    - 추가
- 		<dependency>
- 			<groupId>org.projectlombok</groupId>
- 			<artifactId>lombok</artifactId>
- 			<optional>true</optional>
- 		</dependency>
-   변경
-       <java.version>1.8</java.version>
-   변경
-       <version>2.4.9</version>

[New]
4. google api console 이동
    - New Project
    - OAuth 동의화면
    - 외부 > 만들기
    - 애플리케이션명, 이메일 적고 > 저장.
    - 사용자 인증정보
    - 사용자 인증정보 만들기 > OAuth 클라이언트 ID 만들기
      - 유형: 웹애플리케이션
      - 이름: springboot-oauth
      - 리다이렉션URL: http://localhost:8888/login/oauth2/code/google
        - 구글 로그인 완료> 구글서버에서 코드를 돌려줌.
        - 코드를 통해 Access Token을 요청
        - Access Token은 고객이 google의 접근할 수있는 Token
        - 해당 코드를 받을수 있는 URL
        - 이 주소("/login/oauth2/code")는 고정.
        - 해당 URL의 Controller를 만들 필요는 없음. 라이브러리가 처리
      - 만들기

5. 해당 라이브러리 다운.
    - springboot oauth
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-oauth2-client</artifactId>
    </dependency>

6. application 설정.
    - application 등록.(OAuth)

7. loginForm 수정
    - "/oauth2/authorization/google" 해당 주소 로그인.

8. PrincipalOauth2UserService 생성.
    - 강제회원가입

9. IndexContorller
    - testLogin 함수 확인
    - testOAuthLogin 함수 확인
    
10. Spring Security
    - Security Session
        - Authentication
            - (getPrincipal) -> UserDetails (일반 로그인)
            - (getPrincipal) -> OAuth2User  (OAuth2 로그인)

    - X 라는 Class를 만들어서.
        - UserDetails를 implements한 PrincipalDetails Class에 
        - OAuth2User를 implements한다.
        - public class PrincipalDetails implements UserDetails, OAuth2User
            - 회원가입 (User)

11. PrincipalDetailsService 와 PrincipalOauth2UserService 만든 이유.
    - 만들지 않아도 아래 함수를 자동으로 호출.
        - PrincipalDetailsService: public UserDetails loadUserByUsername(String username)
        - PrincipalOauth2UserService: public OAuth2User loadUser(OAuth2UserRequest userRequest)
        - 굳이 오버라이드 하여 만든 이유는 
            - UserDetails, OAuth2User을 implements한 PrincipalDetails를 생성하기 위해서,,,!
            - PrincipalDetails 생성하여 가입한 유저의 정보인 User Object를 얻기 위해서!
            - User(Object)는 MySQL에 등록되는 회원 원장 객체이다.
            - User는 PrincipalDetails 안에 살아 숨쉬고 있다,,,
        - 추가로 PrincipalOauth2UserService 에서 로그인된 회원을 강제 회원가입.

[New]
12. facebook api console 이동
    - 앱 만들기 > 유형: 기타(없음)
    - 설정 > 기본설정
    - 문서: 웹로그인.

[New]
13. provider 제공자! (구글, 네이버, 페이스북, 트위터, 카카오)

14. 네이버 개발자 사이트
    - 앱 등록
    - PC 웹
    - 서비스URL, CallBackUrl 등록.
