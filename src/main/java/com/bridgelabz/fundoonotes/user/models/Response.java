package com.bridgelabz.fundoonotes.user.models;

import org.springframework.stereotype.Component;

@Component
public class Response {

	private String message;
	private int status;

	public Response() {
		super();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
