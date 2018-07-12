package com.bridgelabz.fundonotes.usermodule.service;

import org.springframework.stereotype.Repository;

import com.bridgelabz.fundonotes.usermodule.exception.LoginException;
import com.bridgelabz.fundonotes.usermodule.exception.RegistrationException;
import com.bridgelabz.fundonotes.usermodule.model.LoginDTO;
import com.bridgelabz.fundonotes.usermodule.model.RegistrationDTO;
import com.bridgelabz.fundonotes.usermodule.model.ResponseDTO;

@Repository
public interface UserService {

	public ResponseDTO register(RegistrationDTO registrationDto)throws RegistrationException;
	public ResponseDTO login(LoginDTO loginDto)throws LoginException;
	
}
