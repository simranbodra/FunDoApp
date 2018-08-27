package com.bridgelabz.fundoonotes.user.models;

public class UserProfile {

	private String userName;
	private String userEmail;
	private String imageUrl;

	public UserProfile() {
		super();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Override
	public String toString() {
		return "UserProfile [userName=" + userName + ", userEmail=" + userEmail + ", imageUrl=" + imageUrl + "]";
	}

}
