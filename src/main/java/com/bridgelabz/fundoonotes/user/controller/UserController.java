package com.bridgelabz.fundoonotes.user.controller;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.bridgelabz.fundoonotes.user.exceptions.LoginException;
import com.bridgelabz.fundoonotes.user.exceptions.RegistrationException;
import com.bridgelabz.fundoonotes.user.models.Login;
import com.bridgelabz.fundoonotes.user.models.Registration;
import com.bridgelabz.fundoonotes.user.models.ResetPassword;
import com.bridgelabz.fundoonotes.user.models.Response;
import com.bridgelabz.fundoonotes.user.services.UserService;

@RestController
public class UserController {


	@Autowired
	UserService userService;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<Response> register(@RequestBody Registration registration)
			throws RegistrationException, MessagingException {
		userService.register(registration);
		
		Response response = new Response();
		response.setMessage("Registration Successful");
		response.setStatus(10);
		
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<Response> login(@RequestBody Login login,HttpServletResponse resp) throws LoginException {
		
		String token = userService.login(login);
		resp.setHeader("token", token);
		Response response = new Response();
		response.setMessage("Login Successful");
		response.setStatus(20);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value="/activateaccount", method = RequestMethod.GET)
	public ResponseEntity<Response> activateaccount(@RequestHeader("token") String  token) throws LoginException {

		userService.activate(token);
		
		Response response = new Response();
		response.setMessage("Account activated successfully");
		response.setStatus(12);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value="/resetPasswordLink", method = RequestMethod.GET)
	public ResponseEntity<Response> resetPasswordLink(@RequestParam("email") String email ) throws MessagingException{
		
		userService.sendPasswordLink(email);
		
		Response response = new Response();
		response.setMessage("Successfully sent mail");
		response.setStatus(31);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value="/resetPassword", method = RequestMethod.PUT)
	public ResponseEntity<Response> resetPassword(@RequestHeader("token") String  token, @RequestBody ResetPassword resetPassword) throws LoginException{
		userService.passwordReset(token, resetPassword);
		
		Response response = new Response();
		response.setMessage("Password reset successful");
		response.setStatus(31);
		
		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}
}
