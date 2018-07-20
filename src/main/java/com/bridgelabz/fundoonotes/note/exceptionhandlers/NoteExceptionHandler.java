package com.bridgelabz.fundoonotes.note.exceptionhandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.bridgelabz.fundoonotes.note.exceptions.NoteException;
import com.bridgelabz.fundoonotes.user.models.Response;

public class NoteExceptionHandler {

private final Logger logger = LoggerFactory.getLogger(NoteExceptionHandler.class);
	
	@ExceptionHandler(NoteException.class)
	public ResponseEntity<Response> handleRegistrationException(NoteException exception) {
		logger.info("Error occured while creating new node " + exception.getMessage(), exception);

		Response response = new Response();
		response.setMessage(exception.getMessage());
		response.setStatus(91);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
}
