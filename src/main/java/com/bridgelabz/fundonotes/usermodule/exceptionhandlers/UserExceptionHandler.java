package com.bridgelabz.fundonotes.usermodule.exceptionhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.bridgelabz.fundonotes.usermodule.exception.LoginException;
import com.bridgelabz.fundonotes.usermodule.exception.RegistrationException;
import com.bridgelabz.fundonotes.usermodule.model.ResponseDTO;

@ControllerAdvice
public class UserExceptionHandler {

	private final Logger logger = LoggerFactory.getLogger(UserExceptionHandler.class);

	@ExceptionHandler(RegistrationException.class)
	public ResponseEntity<?> handleRegistrationException(RegistrationException exception) {
		logger.info("Error occured while registration " + exception.getMessage(), exception);

		ResponseDTO response = new ResponseDTO();
		response.setMessage(exception.getMessage());
		response.setStatus(1);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(LoginException.class)
	public ResponseEntity<?> handleRegistrationException(LoginException exception) {
		logger.info("Error occured while registration " + exception.getMessage(), exception);

		ResponseDTO response = new ResponseDTO();
		response.setMessage(exception.getMessage());
		response.setStatus(2);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleRegistrationException(Exception exception){
		logger.error("Error occured for " + exception.getMessage(), exception);

		ResponseDTO response = new ResponseDTO();
		response.setMessage("Something went wrong");
		response.setStatus(0);

		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
}
}
