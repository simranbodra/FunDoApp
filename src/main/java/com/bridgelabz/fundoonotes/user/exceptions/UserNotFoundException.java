package com.bridgelabz.fundoonotes.user.exceptions;

public class UserNotFoundException extends Exception{
	
	private static final long serialVersionUID = -5525094865793090369L;

	public UserNotFoundException(String message) {
		super(message);
	}
}
