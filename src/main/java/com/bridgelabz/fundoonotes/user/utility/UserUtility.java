package com.bridgelabz.fundoonotes.user.utility;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import com.bridgelabz.fundoonotes.user.exceptions.LoginException;
import com.bridgelabz.fundoonotes.user.exceptions.RegistrationException;
import com.bridgelabz.fundoonotes.user.models.Login;
import com.bridgelabz.fundoonotes.user.models.Registration;
import com.bridgelabz.fundoonotes.user.models.ResetPassword;
import com.bridgelabz.fundoonotes.user.models.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class UserUtility {

	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern
			.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$");

	private static final Pattern PASSWORD_PATTERN = Pattern
			.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20})");

	private static final Pattern CONTACT_PATTERN = Pattern.compile("^[0-9]{10}$");

	private UserUtility() {

	}

	/**
	 * 
	 * @param userId
	 * @return
	 */
	public static String tokenGenerator(String userId) {
		String key = "simran";

		long nowMillis = System.currentTimeMillis() + (20 * 60 * 60 * 1000);
		Date now = new Date(nowMillis);

		JwtBuilder builder = Jwts.builder().setId(userId).setIssuedAt(now).setSubject(userId)
				.signWith(SignatureAlgorithm.HS256, key);

		return builder.compact();
	}

	public static String parseJWT(String jwt) {
		String key = "simran";

		Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(key)).parseClaimsJws(jwt)
				.getBody();

		return claims.getId();
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

	public static void validateUserForRegistration(Registration registrationDto) throws RegistrationException {
		if (registrationDto.getUserName() == null || registrationDto.getUserName().length() < 3) {
			throw new RegistrationException("Name should have atleast 3 characters");
		}

		if (registrationDto.getPhoneNumber() == null || (!validatePhoneNumber(registrationDto.getPhoneNumber()))) {
			throw new RegistrationException("Contact number should be exactly 10 digit numbers");
		}

		if (registrationDto.getPassword() == null || (!validatePassword(registrationDto.getPassword()))) {
			throw new RegistrationException("Password should have atleast one uppercase character, "
					+ "atlest one lowercase character, " + "one special character, " + "and atleast one number");
		}

		if (registrationDto.getEmailId() == null || registrationDto.getEmailId().length() == 0) {
			throw new RegistrationException("Incorrect email format");
		}

		if (registrationDto.getConfirmPassword() == null || registrationDto.getConfirmPassword().length() == 0
				|| (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword()))) {
			throw new RegistrationException("password should match with confirm password");
		}
	}

	public static User getUser(Registration registrationDto) {
		User user = new User();
		user.setEmail(registrationDto.getEmailId());
		user.setName(registrationDto.getUserName());
		user.setPhoneNumber(registrationDto.getPhoneNumber());

		user.setPassword(registrationDto.getPassword());
		return user;
	}

	public static void validateUserForLogin(Login loginDto) throws LoginException {
		if (loginDto.getEmail() == null || loginDto.getEmail().length() == 0) {
			throw new LoginException("Incorrect email format");
		}

		if (loginDto.getPassword() == null || (!validatePassword(loginDto.getPassword()))) {
			throw new LoginException("Password should have atleast one uppercase character, "
					+ "atlest one lowercase character, " + "one special character, " + "and atleast one number");
		}
	}

	public static void validateUserForResetPassword(ResetPassword resetPassword) throws LoginException {
		if (resetPassword.getPassword() == null || (!validatePassword(resetPassword.getPassword()))) {
			throw new LoginException("Password should have atleast one uppercase character, "
					+ "atlest one lowercase character, " + "one special character, " + "and atleast one number");
		}

		if (resetPassword.getConfirmPassword() == null
				|| (!resetPassword.getPassword().equals(resetPassword.getConfirmPassword()))) {
			throw new LoginException("password should match with confirm password");
		}
	}
}
