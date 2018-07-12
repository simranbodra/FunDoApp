package com.bridgelabz.fundonotes.usermodule.model;

public class ResponseDTO {

	private String message;
	private int status;

	public ResponseDTO() {
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
