package com.bridgelabz.fundoonotes.user.services;

import java.util.Optional;

import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundoonotes.user.exceptions.LoginException;
import com.bridgelabz.fundoonotes.user.exceptions.RegistrationException;
import com.bridgelabz.fundoonotes.user.models.Login;
import com.bridgelabz.fundoonotes.user.models.Mail;
import com.bridgelabz.fundoonotes.user.models.Registration;
import com.bridgelabz.fundoonotes.user.models.ResetPassword;
import com.bridgelabz.fundoonotes.user.models.User;
import com.bridgelabz.fundoonotes.user.rabbitmq.MailProducerService;
import com.bridgelabz.fundoonotes.user.repositories.UserRepository;
import com.bridgelabz.fundoonotes.user.utility.UserUtility;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository repository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private MailProducerService producer;

	@Value("${activationLink}")
	private String activationLink;
	
	@Value("${resetPasswordLink}")
	private String resetPasswordLink;



	@Override
	public void register(Registration registrationDto) throws RegistrationException, MessagingException {
		UserUtility.validateUserForRegistration(registrationDto);
		
		if (repository.findByEmail(registrationDto.getEmailId()).isPresent()) {
			throw new RegistrationException("Email already registered");
		}
		
		User user = new User();
		user.setEmail(registrationDto.getEmailId());
		user.setName(registrationDto.getUserName());
		user.setPhoneNumber(registrationDto.getPhoneNumber());
		
		String password = passwordEncoder.encode(registrationDto.getPassword());
		
		user.setPassword(password);
		
		repository.save(user);
		
		String token = UserUtility.tokenGenerator(user.getUserId());
		Mail mail = new Mail();
		mail.setTo(user.getEmail());
		mail.setSubject("Account Activation Mail");
		mail.setBody(activationLink + token);
		producer.send(mail);
	}

	@Override
	public String login(Login loginDto) throws LoginException {
		UserUtility.validateUserForLogin(loginDto);
		
		Optional<User> user = repository.findByEmail(loginDto.getEmail());
		
		if(!user.isPresent()) {
			throw new LoginException("Email not registered");
		}
		
		if(!user.get().isActive()) {
			throw new LoginException("Please Activate your account");
		}
		
		if(!passwordEncoder.matches(loginDto.getPassword(),user.get().getPassword())) {
			throw new LoginException("Incorrect Password");
		}
		
		String token = UserUtility.tokenGenerator(user.get().getUserId());
		
		return token;
		
	}

	@Override
	public void activate(String token) throws LoginException {
		String userId = UserUtility.parseJWT(token);
		
		
		Optional<User> optionalUser = repository.findById(userId);
		User user = optionalUser.get();
		user.setActive(true);
		repository.save(user);
		
	}
	
	@Override
	public void sendPasswordLink(String email) throws MessagingException {
		
		Optional<User> user = repository.findByEmail(email);
		String token = UserUtility.tokenGenerator(user.get().getUserId());
		
		Mail mail = new Mail();
		mail.setTo(email);
		mail.setSubject("Account Activation Mail");
		mail.setBody(resetPasswordLink + token);
		producer.send(mail);
	}
	
	@Override
	public void passwordReset(String token, ResetPassword resetPassword)throws LoginException {
		UserUtility.validateUserForResetPassword(resetPassword);
		
		String userId = UserUtility.parseJWT(token);
		
		if (!repository.existsById(userId)) {
			throw new LoginException("Email is not registered");
		}
		
		Optional<User> optionalUser = repository.findById(userId);
		
		User user = new User();
		if (optionalUser.isPresent()) {
			user.setUserId(optionalUser.get().getUserId());
			user.setName(optionalUser.get().getName());
			user.setEmail(optionalUser.get().getEmail());
			user.setPhoneNumber(optionalUser.get().getPhoneNumber());
			
			String password = passwordEncoder.encode(resetPassword.getPassword());
			
			user.setPassword(password);
			user.setActive(true);
			
			repository.save(user);
		}

	}
}
