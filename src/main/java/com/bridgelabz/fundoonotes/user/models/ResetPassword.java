package com.bridgelabz.fundoonotes.user.models;

public class ResetPassword {

	private String password;
	private String confirmPassword;

	public ResetPassword() {
		super();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

}
