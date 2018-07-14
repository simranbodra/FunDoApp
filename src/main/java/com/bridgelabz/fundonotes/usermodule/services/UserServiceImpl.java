package com.bridgelabz.fundonotes.usermodule.services;

import java.util.Optional;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundonotes.usermodule.exceptions.LoginException;
import com.bridgelabz.fundonotes.usermodule.exceptions.RegistrationException;
import com.bridgelabz.fundonotes.usermodule.models.Login;
import com.bridgelabz.fundonotes.usermodule.models.Mail;
import com.bridgelabz.fundonotes.usermodule.models.Registration;
import com.bridgelabz.fundonotes.usermodule.models.User;
import com.bridgelabz.fundonotes.usermodule.repositories.UserRepository;
import com.bridgelabz.fundonotes.usermodule.utility.UserUtility;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private MailService mailService;
	
	@Value("${link}")
	private String link;

	@Override
	public void register(Registration registrationDto) throws RegistrationException, AddressException, MessagingException {
		UserUtility.validateUserForRegistration(registrationDto);
		if (repository.existsById(registrationDto.getEmailId())) {
			throw new RegistrationException("Email already registered");
		}
		User user = UserUtility.getUser(registrationDto);
		repository.save(user);
		String token = UserUtility.tokenGenerator(user.getEmail());
		Mail mail = new Mail();
		mail.setTo(user.getEmail());
		mail.setSubject("Account Activation Mail");
		mail.setBody(link + token);
		mailService.sendActivationLink(mail);
	}

	@Override
	public void login(Login loginDto) throws LoginException {
		UserUtility.validateUserForLogin(loginDto);
		Optional<User> user = null;
		if (!repository.existsById(loginDto.getEmail())) {
			throw new LoginException("Email not registered");
		}
		user = repository.findById(loginDto.getEmail());
		if (!user.get().getPassword().equals(loginDto.getPassword())) {
			throw new LoginException("Incorrect password");
		}
	}
	
	@Override
	public void activate(String token) throws LoginException {
		String email = UserUtility.parseJWT(token);
		if(!repository.existsById(email)) {
			throw new LoginException("Email is not registered");
		}
		Optional<User> optionalUser = repository.findById(email);
		User user = new User();
		user.setUserId(optionalUser.get().getUserId());
		user.setName(optionalUser.get().getName());
		user.setEmail(optionalUser.get().getEmail());
		user.setPhoneNumber(optionalUser.get().getPhoneNumber());
		user.setPassword(optionalUser.get().getPassword());
		user.setStatus(true);
		repository.save(user);
	}
}
