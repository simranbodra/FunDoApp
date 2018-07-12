package com.bridgelabz.fundonotes.usermodule.utility;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.bridgelabz.fundonotes.usermodule.exception.LoginException;
import com.bridgelabz.fundonotes.usermodule.exception.RegistrationException;
import com.bridgelabz.fundonotes.usermodule.model.LoginDTO;
import com.bridgelabz.fundonotes.usermodule.model.RegistrationDTO;
import com.bridgelabz.fundonotes.usermodule.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@PropertySource("classpath:application.properties")
public class UserUtility {

	@Value("${spring.mail.username}")
	private String from;
	@Value("${spring.mail.password}")
	private String password;
	@Value("${spring.mail.host}")
	private String host;
	
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern
			.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$");
	
	private static final Pattern PASSWORD_PATTERN = Pattern
			.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20})");
	
	private static final Pattern CONTACT_PATTERN = Pattern.compile("^[0-9]{10}$");

	public static String tokenGenerator(User user) {
		String key = "key";
		String email = user.getName();
		String passkey = user.getPassword();
		long nowMillis = System.currentTimeMillis() + (20 * 60 * 60 * 1000);
		Date now = new Date(nowMillis);
		JwtBuilder builder = Jwts.builder().setId(passkey).setIssuedAt(now).setSubject(email)
				.signWith(SignatureAlgorithm.HS256, key);
		return builder.compact();
	}

	public static void parseJWT(String jwt) {
		String key = "key";

		Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(key)).parseClaimsJws(jwt)
				.getBody();
		System.out.println("ID: " + claims.getId());
		System.out.println("Subject: " + claims.getSubject());
	}

	public static boolean validateEmail(String email) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	public static boolean validatePassword(String password) {
		Matcher matcher = PASSWORD_PATTERN.matcher(password);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	public static boolean validatePhoneNumber(String phoneNumber) {
		Matcher matcher = CONTACT_PATTERN.matcher(phoneNumber);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	public static String getRequestUrl(HttpServletRequest request) throws MalformedURLException {
		URL url = new URL(request.getRequestURL().toString());
		return url.getProtocol() + "://" + url.getPort() + request.getContextPath();
	}

	public static void validateUserForRegistration(RegistrationDTO registrationDto) throws RegistrationException {
		if (registrationDto.getUserId() == null || registrationDto.getUserId().length() < 4) {
			throw new RegistrationException("Enter 4 digit social security number");
		}
		if (registrationDto.getUserName() == null || registrationDto.getUserName().length() < 3) {
			throw new RegistrationException("Name should have atleast 3 characters");
		}
		if (registrationDto.getPhoneNumber() == null || (!validatePhoneNumber(registrationDto.getPhoneNumber()))) {
			throw new RegistrationException("Contact number should be exactly 10 digit numbers");
		}
		if (registrationDto.getPassword() == null || (!validatePassword(registrationDto.getPassword()))) {
			throw new RegistrationException("Password should have atleast one uppercase character /n"
					+ "atlest one lowercase character /n" + "one special character /n" + "atleast one number");
		}
		if (registrationDto.getEmailId() == null) {
			throw new RegistrationException("Incorrect email format");
		}
		if (registrationDto.getConfirmPassword() == null || (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword()))) {
			throw new RegistrationException("password should match with confirm password");
		}
	}

	public static User getUser(RegistrationDTO registrationDto) {
		User user = new User();
		user.setUserId(registrationDto.getUserId());
		user.setEmail(registrationDto.getEmailId());
		user.setName(registrationDto.getUserName());
		user.setPhoneNumber(registrationDto.getPhoneNumber());
		user.setPassword(registrationDto.getPassword());
		return user;
	}

	public static void validateUserForLogin(LoginDTO loginDto) throws LoginException {
		if (loginDto.getEmail() == null) {
			throw new LoginException("Incorrect email format");
		}
	}
	
	public static void sendActivationLink(String jwToken, User user) {
		String to = user.getEmail();
		String subject = "EmailActivation mail";
		String body = "Click here to activate your account:\n\n"
				+ "http://192.168.0.36:8080/LoginRegister/activateaccount/?" + jwToken;
		Properties props = System.getProperties();
		
	}
}
