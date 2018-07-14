package com.bridgelabz.fundonotes.usermodule.controller;

import java.net.MalformedURLException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.fundonotes.usermodule.exceptions.LoginException;
import com.bridgelabz.fundonotes.usermodule.exceptions.RegistrationException;
import com.bridgelabz.fundonotes.usermodule.models.Login;
import com.bridgelabz.fundonotes.usermodule.models.Registration;
import com.bridgelabz.fundonotes.usermodule.models.Response;
import com.bridgelabz.fundonotes.usermodule.services.UserService;

@RestController
@RequestMapping("/LoginRegister")
public class UserController {

	public static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<Response> register(@RequestBody Registration registrationDto)
			throws MalformedURLException, RegistrationException, AddressException, MessagingException {
		logger.info("Registering User : {}", registrationDto.getEmailId());
		// String url = UserUtility.getRequestUrl(request);
		userService.register(registrationDto);
		logger.info("User Successfully registered in : {}", registrationDto.getEmailId());
		Response response = new Response();
		response.setMessage("Registration Successful");
		response.setStatus(10);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<Response> login(@RequestBody Login loginDto) throws LoginException {
		logger.info("Logging User : {}", loginDto.getEmail());
		userService.login(loginDto);
		logger.info("User Successfully logged in : {}", loginDto.getEmail());
		Response response = new Response();
		response.setMessage("Login Successful");
		response.setStatus(20);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value="/activateaccount", method = RequestMethod.GET)
	//@RequestParam(value = "token"
	public ResponseEntity<Response> activateaccount(HttpServletRequest request) throws LoginException {
		String token = request.getQueryString();

		userService.activate(token);
		Response response = new Response();
		response.setMessage("Account activated successfully");
		response.setStatus(12);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
