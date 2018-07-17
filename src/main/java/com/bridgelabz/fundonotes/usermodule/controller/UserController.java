package com.bridgelabz.fundonotes.usermodule.controller;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.fundonotes.usermodule.exceptions.LoginException;
import com.bridgelabz.fundonotes.usermodule.exceptions.RegistrationException;
import com.bridgelabz.fundonotes.usermodule.models.Login;
import com.bridgelabz.fundonotes.usermodule.models.Registration;
import com.bridgelabz.fundonotes.usermodule.models.ResetPassword;
import com.bridgelabz.fundonotes.usermodule.models.Response;
import com.bridgelabz.fundonotes.usermodule.services.UserService;

@RestController
@RequestMapping("/LoginRegister")
public class UserController {

	public static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;
	
	@Autowired
	private Response response;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<Response> register(@RequestBody Registration registration)
			throws RegistrationException, MessagingException {
		logger.info("Registering User : {}", registration.getEmailId());
		userService.register(registration);
		logger.info("User Successfully registered in : {}", registration.getEmailId());
		response.setMessage("Registration Successful");
		response.setStatus(10);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<Response> login(@RequestBody Login login) throws LoginException {
		
		logger.info("Logging User : {}", login.getEmail());
		userService.login(login);
		logger.info("User Successfully logged in : {}", login.getEmail());
		
		response.setMessage("Login Successful");
		response.setStatus(20);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value="/activateaccount", method = RequestMethod.GET)
	public ResponseEntity<Response> activateaccount(HttpServletRequest request) throws LoginException {
		String token = request.getQueryString();

		userService.activate(token);
		
		response.setMessage("Account activated successfully");
		response.setStatus(12);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value="/resetPasswordLink", method = RequestMethod.GET)
	public ResponseEntity<Response> resetPasswordLink(@RequestParam("email") String email ) throws MessagingException{
		logger.info("Reset Password link requested by user with {}", email);
		userService.sendPasswordLink(email);
		response.setMessage("Successfully sent mail");
		response.setStatus(31);
		logger.info("Successfully sent mail to user with {}", email);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value="/resetPassword", method = RequestMethod.PUT)
	public ResponseEntity<Response> resetPassword(@RequestHeader("token") String  token, @RequestBody ResetPassword resetPassword) throws LoginException{
		userService.passwordReset(token, resetPassword);
		
		response.setMessage("Password reset successful");
		response.setStatus(31);
		logger.info("Successfully reset the password");
		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}
}
