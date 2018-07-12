package com.bridgelabz.fundonotes.usermodule.controller;

import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.fundonotes.usermodule.exception.LoginException;
import com.bridgelabz.fundonotes.usermodule.exception.RegistrationException;
import com.bridgelabz.fundonotes.usermodule.model.LoginDTO;
import com.bridgelabz.fundonotes.usermodule.model.RegistrationDTO;
import com.bridgelabz.fundonotes.usermodule.model.ResponseDTO;
import com.bridgelabz.fundonotes.usermodule.service.UserService;
import com.bridgelabz.fundonotes.usermodule.service.UserServiceImpl;

@RestController
@RequestMapping("/LoginRegister")
public class UserController {
	
	public static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	UserService userService;

	@RequestMapping(value = "/register/", method = RequestMethod.POST)
	public ResponseEntity<?> register(@RequestBody RegistrationDTO registrationDto)throws MalformedURLException, RegistrationException {
		logger.info("Registering User : {}", registrationDto);
		// String url = UserUtility.getRequestUrl(request);
		ResponseDTO response = userService.register(registrationDto);
		logger.info("User Successfully registered in : {}", registrationDto);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/login/", method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody LoginDTO loginDto) throws LoginException {
		logger.info("Logging User : {}", loginDto);
		ResponseDTO response = userService.login(loginDto);
		logger.info("User Successfully logged in : {}", loginDto);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	
}
