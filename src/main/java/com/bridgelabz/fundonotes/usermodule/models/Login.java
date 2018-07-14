package com.bridgelabz.fundonotes.usermodule.models;

public class Login {

	private String emailId;

	private String password;

	public Login() {
		super();
	}

	public String getEmail() {
		return emailId;
	}

	public void setEmail(String email) {
		this.emailId = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "LoginDTO [email=" + emailId + ", password=" + password + "]";
	}

}
