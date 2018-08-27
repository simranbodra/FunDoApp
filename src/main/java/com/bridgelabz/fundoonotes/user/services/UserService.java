package com.bridgelabz.fundoonotes.user.services;

import javax.mail.MessagingException;

import org.springframework.web.multipart.MultipartFile;

import com.bridgelabz.fundoonotes.user.exceptions.FileConversionException;
import com.bridgelabz.fundoonotes.user.exceptions.LoginException;
import com.bridgelabz.fundoonotes.user.exceptions.RegistrationException;
import com.bridgelabz.fundoonotes.user.exceptions.UserNotActivatedException;
import com.bridgelabz.fundoonotes.user.exceptions.UserNotFoundException;
import com.bridgelabz.fundoonotes.user.models.Login;
import com.bridgelabz.fundoonotes.user.models.Registration;
import com.bridgelabz.fundoonotes.user.models.ResetPassword;
import com.bridgelabz.fundoonotes.user.models.UserProfile;

public interface UserService {

	public void register(Registration registrationDto)
			throws RegistrationException, MessagingException;

	public String login(Login loginDto) throws LoginException, UserNotFoundException, UserNotActivatedException;

	public void activate(String token) throws LoginException;

	public void sendPasswordLink(String email) throws MessagingException, UserNotFoundException;

	public void passwordReset(String email, ResetPassword resetPassword) throws LoginException, UserNotFoundException;

	public String addProfilePicture(String token, MultipartFile image) throws FileConversionException;

	public void removeProfilePicture(String token);

	public UserProfile getProfileDetails(String token);

}
