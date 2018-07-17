package com.bridgelabz.fundonotes.usermodule.services;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import com.bridgelabz.fundonotes.usermodule.exceptions.LoginException;
import com.bridgelabz.fundonotes.usermodule.exceptions.RegistrationException;
import com.bridgelabz.fundonotes.usermodule.models.Login;
import com.bridgelabz.fundonotes.usermodule.models.Registration;
import com.bridgelabz.fundonotes.usermodule.models.ResetPassword;

public interface UserService {

	public void register(Registration registrationDto)throws RegistrationException, AddressException, MessagingException;
	public void login(Login loginDto)throws LoginException;
	public void activate(String token)throws LoginException;
	public void sendPasswordLink(String email) throws MessagingException;
	public void passwordReset(String email, ResetPassword resetPassword) throws LoginException;
	
}
