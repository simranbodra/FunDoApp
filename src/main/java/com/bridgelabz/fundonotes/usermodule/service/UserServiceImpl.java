package com.bridgelabz.fundonotes.usermodule.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundonotes.usermodule.exception.LoginException;
import com.bridgelabz.fundonotes.usermodule.exception.RegistrationException;
import com.bridgelabz.fundonotes.usermodule.model.LoginDTO;
import com.bridgelabz.fundonotes.usermodule.model.RegistrationDTO;
import com.bridgelabz.fundonotes.usermodule.model.ResponseDTO;
import com.bridgelabz.fundonotes.usermodule.model.User;
import com.bridgelabz.fundonotes.usermodule.repository.UserRepository;
import com.bridgelabz.fundonotes.usermodule.utility.UserUtility;

@Service
public class UserServiceImpl implements UserService{
	
	
	@Autowired
	UserRepository repository;
	
	@Override
	public ResponseDTO register(RegistrationDTO registrationDto) throws RegistrationException {
		UserUtility.validateUserForRegistration(registrationDto);
		User user = UserUtility.getUser(registrationDto);
		ResponseDTO response = new ResponseDTO();
		if(repository.existsById(user.getEmail())) {
			throw new RegistrationException("Email already registered");
		}
		else {
			repository.save(user);
			String token = UserUtility.tokenGenerator(user);
			response.setMessage("Successfully Registered");
			response.setStatus(11);
			return response;
		}
	}
	
	@Override
	public ResponseDTO login(LoginDTO loginDto) throws LoginException {
		UserUtility.validateUserForLogin(loginDto);
		Optional<User> user = null;
		ResponseDTO response = new ResponseDTO();
		if (repository.existsById(loginDto.getEmail())) {
			user = repository.findById(loginDto.getEmail());
			if (user.get().getPassword().equals(loginDto.getPassword())) {
				response.setMessage("Successfully logged in");
				response.setStatus(21);
				return response;
			}
			else {
				throw new LoginException("Incorrect password");
			}
		}
		throw new LoginException("Email not registered");
	}
}
