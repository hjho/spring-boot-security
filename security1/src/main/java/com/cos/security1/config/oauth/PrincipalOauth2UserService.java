package com.cos.security1.config.oauth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.config.oauth.provider.FacebookUserInfo;
import com.cos.security1.config.oauth.provider.GoogleUserInfo;
import com.cos.security1.config.oauth.provider.NaverUserInfo;
import com.cos.security1.config.oauth.provider.OAuth2UserInfo;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@SuppressWarnings("unchecked")
	// OAuth 후처리되는 함수.
	// 구글로부터 받은 userRequest 데이터에 대한 후처리되는 함수.
	// 함수 종료 시 @AuthenticationPrincipal 어노테이션이 만들어진다.
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		// resgistrationId로 어떤 OAuth로 로그인 했는지 확인 가능.
		System.out.println("## OAuth getClientRegistration: " + userRequest.getClientRegistration());
		// 지금은 별로 중요하지 않음.
		System.out.println("## OAuth getAccessToken: " + userRequest.getAccessToken().getTokenValue());

		OAuth2User oauth2User = super.loadUser(userRequest);
		// 구글로그인 버튼 클릭 -> 로그인 창 -> 로그인 완료 -> Code리턴 (OAuth2-Client라이브러리) -> AccessToken요청
		// userRequest정보 -> loadUser함수 호출 -> 구글로부터 회원 프로필 받아준다.
		System.out.println("## OAuth getAttributes: " + oauth2User.getAttributes());

		OAuth2UserInfo oauth2UserInfo = null;
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		System.out.println("## OAuth "+ registrationId +" 로그인 요청");
		if("google".equals(registrationId)) {
			oauth2UserInfo = new GoogleUserInfo(oauth2User.getAttributes());
			
		} else if("facebook".equals(registrationId)) {
			oauth2UserInfo = new FacebookUserInfo(oauth2User.getAttributes());
			
		} else if("naver".equals(registrationId)) {
			oauth2UserInfo = new NaverUserInfo((Map<String, Object>) oauth2User.getAttributes().get("response"));
			
		} else {
			System.out.println("## OAuth 우리는 구글, 페이스북, 네이버만 지원합니다. ㅎㅎ");
			return null;
		}
		// 강제 회원가입 진행.
		String provider   = oauth2UserInfo.getProvider();
		String providerId = oauth2UserInfo.getProviderId();
		String username   = provider.concat("_").concat(providerId);
		String password   = bCryptPasswordEncoder.encode("패스워드");
		String email      = oauth2UserInfo.getEmail();
		String role       = "ROLE_USER";
		
		User userEntity = userRepository.findByUsername(username);
		
		if(userEntity == null) {
			System.out.println("## OAuth 로그인이 최초 입니다.");
			userEntity = User.builder()
					.username(username)
					.password(password)
					.email(email)
					.role(role)
					.provider(provider)
					.providerId(providerId)
					.build();
			userRepository.save(userEntity);
		} else {
			System.out.println("## OAuth 로그인을 이미 한적이 있어 당신은 자동회원가입 되어 있습니다.");
		}
		
		System.out.println("## OAuth userEntity: " + userEntity);
		return new PrincipalDetails(userEntity, oauth2User.getAttributes());
	}
}
