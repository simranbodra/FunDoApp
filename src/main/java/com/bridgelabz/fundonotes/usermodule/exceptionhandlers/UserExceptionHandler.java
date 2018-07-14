package com.bridgelabz.fundonotes.usermodule.exceptionhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.bridgelabz.fundonotes.usermodule.exceptions.LoginException;
import com.bridgelabz.fundonotes.usermodule.exceptions.RegistrationException;
import com.bridgelabz.fundonotes.usermodule.models.Response;

@ControllerAdvice
public class UserExceptionHandler {

	private final Logger logger = LoggerFactory.getLogger(UserExceptionHandler.class);
	
	@Autowired
	private Response response;

	@ExceptionHandler(RegistrationException.class)
	public ResponseEntity<Response> handleRegistrationException(RegistrationException exception) {
		logger.error("Error occured while registration " + exception.getMessage(), exception);

		response.setMessage(exception.getMessage());
		response.setStatus(1);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(LoginException.class)
	public ResponseEntity<Response> handleLoginException(LoginException exception) {
		logger.error("Error occured while login " + exception.getMessage(), exception);

		response.setMessage(exception.getMessage());
		response.setStatus(2);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response> handleException(Exception exception){
		logger.error("Error occured for " + exception.getMessage(), exception);

		response.setMessage("Something went wrong");
		response.setStatus(0);

		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
}
}
