package com.bridgelabz.fundoonotes.user.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundoonotes.user.models.FacebookUser;
import com.bridgelabz.fundoonotes.user.models.User;
import com.bridgelabz.fundoonotes.user.repositories.UserRepository;
import com.bridgelabz.fundoonotes.user.utility.JWTokenProvider;

@Service
public class FacebookService {

	@Value("${spring.social.facebook.appId}")
	String facebookAppId;
	
	@Value("${spring.social.facebook.appSecret}")
	String facebookSecret;

	String accessToken;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JWTokenProvider tokenProvider;

	public String createFacebookAuthorizationURL() {
		FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookSecret);
		OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
		OAuth2Parameters params = new OAuth2Parameters();
		params.setRedirectUri(
				"http://localhost:8080/swagger-ui.html#!/user-controller/createFacebookAccessTokenUsingGET");
		params.setScope("public_profile,email,user_birthday");
		return oauthOperations.buildAuthorizeUrl(params);
	}

	public String createFacebookAccessToken(String code) {
		FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookSecret);
		AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeForAccess(code,
				"http://localhost:8080/swagger-ui.html#!/user-controller/createFacebookAccessTokenUsingGET", null);
		accessToken = accessGrant.getAccessToken();
		FacebookUser facebookUser = getName();
		
		Optional<User> optionalUser = userRepository.findByEmail(facebookUser.getEmail());
		
		if(optionalUser.isPresent()) {			
			String token = tokenProvider.tokenGenerator(optionalUser.get().getUserId());
			
			return token;
		}
		User user = new User();
		user.setName(facebookUser.getName());
		user.setEmail(facebookUser.getEmail());
		user.setActive(true);
		
		userRepository.save(user);
		
		String token = tokenProvider.tokenGenerator(user.getUserId());
		
		return token;
	}

	public FacebookUser getName() {
		Facebook facebook = new FacebookTemplate(accessToken);
						
		FacebookUser information = facebook.fetchObject("me", FacebookUser.class, "name","email");

		return information;

	}

}
