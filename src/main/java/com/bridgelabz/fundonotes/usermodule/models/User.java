package com.bridgelabz.fundonotes.usermodule.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class User {

	private String _Id;
	
	private String userName;
	
	@Id
	private String userEmail;
	
	private String phoneNumber;
	
	private String password;
	
	private boolean active;

	public User() {
		super();
	}
	
	public String getUserId() {
		return _Id;
	}

	public void setUserId(String userId) {
		this._Id = userId;
	}

	public String getName() {
		return userName;
	}

	public void setName(String name) {
		this.userName = name;
	}

	public String getEmail() {
		return userEmail;
	}

	public void setEmail(String email) {
		this.userEmail = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean isStatus() {
		return active;
	}

	public void setStatus(boolean status) {
		active = status;
	}

}
