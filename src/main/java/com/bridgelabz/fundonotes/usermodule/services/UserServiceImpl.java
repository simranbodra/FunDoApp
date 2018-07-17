package com.bridgelabz.fundonotes.usermodule.services;

import java.util.Optional;

import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundonotes.usermodule.exceptions.LoginException;
import com.bridgelabz.fundonotes.usermodule.exceptions.RegistrationException;
import com.bridgelabz.fundonotes.usermodule.models.Login;
import com.bridgelabz.fundonotes.usermodule.models.Mail;
import com.bridgelabz.fundonotes.usermodule.models.Registration;
import com.bridgelabz.fundonotes.usermodule.models.ResetPassword;
import com.bridgelabz.fundonotes.usermodule.models.User;
import com.bridgelabz.fundonotes.usermodule.rabbitmq.MailConsumer;
import com.bridgelabz.fundonotes.usermodule.rabbitmq.MailProducer;
import com.bridgelabz.fundonotes.usermodule.repositories.UserRepository;
import com.bridgelabz.fundonotes.usermodule.utility.UserUtility;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository repository;

	@Autowired
	private MailService mailService;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private MailProducer producer;
	
	@Autowired
	private MailConsumer consumer;

	@Value("${activationLink}")
	private String activationLink;
	
	@Value("${resetPasswordLink}")
	private String resetPasswordLink;

	@Override
	public void register(Registration registrationDto) throws RegistrationException, MessagingException {
		UserUtility.validateUserForRegistration(registrationDto);
		if (repository.existsById(registrationDto.getEmailId())) {
			throw new RegistrationException("Email already registered");
		}
		User user = new User();
		user.setEmail(registrationDto.getEmailId());
		user.setName(registrationDto.getUserName());
		user.setPhoneNumber(registrationDto.getPhoneNumber());
		String password = passwordEncoder.encode(registrationDto.getPassword());
		user.setPassword(password);
		repository.save(user);
		String token = UserUtility.tokenGenerator(user.getEmail());
		Mail mail = new Mail();
		mail.setTo(user.getEmail());
		mail.setSubject("Account Activation Mail");
		mail.setBody(activationLink + token);
		producer.send(mail);
//		System.out.println(consumer.receiveMail().toString());
//		mailService.sendLink(consumer.receiveMail());
	}

	@Override
	public void login(Login loginDto) throws LoginException {
		UserUtility.validateUserForLogin(loginDto);
		Optional<User> user = repository.findById(loginDto.getEmail());
		if(!user.isPresent()) {
			throw new LoginException("Email not registered");
		}
		if(!user.get().isActive()) {
			throw new LoginException("Please Activate your account");
		}
		if(!passwordEncoder.matches(loginDto.getPassword(),user.get().getPassword())) {
			throw new LoginException("Incorrect Password");
		}
	}

	@Override
	public void activate(String token) throws LoginException {
		String email = UserUtility.parseJWT(token);
		if (!repository.existsById(email)) {
			throw new LoginException("Email is not registered");
		}
		Optional<User> optionalUser = repository.findById(email);
		User user = new User();
		if (optionalUser.isPresent()) {
			user.setUserId(optionalUser.get().getUserId());
			user.setName(optionalUser.get().getName());
			user.setEmail(optionalUser.get().getEmail());
			user.setPhoneNumber(optionalUser.get().getPhoneNumber());
			user.setPassword(optionalUser.get().getPassword());
			user.setActive(true);
			repository.save(user);
		}
	}
	
	@Override
	public void sendPasswordLink(String email) throws MessagingException {
		System.out.println(email);
		String token = UserUtility.tokenGenerator(email);
		Mail mail = new Mail();
		mail.setTo(email);
		mail.setSubject("Account Activation Mail");
		mail.setBody(resetPasswordLink + token);
		producer.send(mail);
		mailService.sendLink(consumer.receiveMail());
	}
	
	@Override
	public void passwordReset(String token, ResetPassword resetPassword)throws LoginException {
		UserUtility.validateUserForResetPassword(resetPassword);
		String userEmail = UserUtility.parseJWT(token);
		if (!repository.existsById(userEmail)) {
			throw new LoginException("Email is not registered");
		}
		Optional<User> optionalUser = repository.findById(userEmail);
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
