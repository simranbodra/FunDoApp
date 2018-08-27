package com.bridgelabz.fundoonotes.user.controller;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bridgelabz.fundoonotes.user.exceptions.FileConversionException;
import com.bridgelabz.fundoonotes.user.exceptions.LoginException;
import com.bridgelabz.fundoonotes.user.exceptions.RegistrationException;
import com.bridgelabz.fundoonotes.user.exceptions.UserNotActivatedException;
import com.bridgelabz.fundoonotes.user.exceptions.UserNotFoundException;
import com.bridgelabz.fundoonotes.user.models.Login;
import com.bridgelabz.fundoonotes.user.models.Registration;
import com.bridgelabz.fundoonotes.user.models.ResetPassword;
import com.bridgelabz.fundoonotes.user.models.Response;
import com.bridgelabz.fundoonotes.user.models.UserProfile;
import com.bridgelabz.fundoonotes.user.services.UserService;

@RestController
public class UserController {

	@Autowired
	UserService userService;

	/**
	 * TO register a user
	 * 
	 * @param registration
	 * @return ResponseDTO
	 * @throws RegistrationException
	 * @throws MessagingException
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<Response> register(@RequestBody Registration registration)
			throws RegistrationException, MessagingException {
		userService.register(registration);

		Response response = new Response();
		response.setMessage("Registration Successful");
		response.setStatus(10);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	/**
	 * For User login
	 * 
	 * @param login
	 * @param resp
	 * @return ResponseDTO
	 * @throws LoginException
	 * @throws UserNotFoundException
	 * @throws UserNotActivatedException
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<Response> login(@RequestBody Login login, HttpServletResponse resp)
			throws LoginException, UserNotFoundException, UserNotActivatedException {

		String token = userService.login(login);

		resp.setHeader("token", token);

		Response response = new Response();
		response.setMessage("Login Successful");
		response.setStatus(20);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * To activate account after registration
	 * 
	 * @param token
	 * @return ResponseDTO
	 * @throws LoginException
	 */
	@RequestMapping(value = "/activateaccount", method = RequestMethod.GET)
	public ResponseEntity<Response> activateaccount(@RequestHeader("token") String token) throws LoginException {

		userService.activate(token);

		Response response = new Response();
		response.setMessage("Account activated successfully");
		response.setStatus(12);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * To send reset password link
	 * 
	 * @param email
	 * @return ResponseDTO
	 * @throws MessagingException
	 * @throws UserNotFoundException
	 */
	@RequestMapping(value = "/resetPasswordLink", method = RequestMethod.GET)
	public ResponseEntity<Response> resetPasswordLink(@RequestParam("email") String email)
			throws MessagingException, UserNotFoundException {

		userService.sendPasswordLink(email);

		Response response = new Response();
		response.setMessage("Successfully sent mail");
		response.setStatus(31);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * To reset password for the user
	 * 
	 * @param token
	 * @param resetPassword
	 * @return ResponseDTO
	 * @throws LoginException
	 * @throws UserNotFoundException
	 */
	@RequestMapping(value = "/resetPassword", method = RequestMethod.PUT)
	public ResponseEntity<Response> resetPassword(@RequestHeader("token") String token,
			@RequestBody ResetPassword resetPassword) throws LoginException, UserNotFoundException {
		userService.passwordReset(token, resetPassword);

		Response response = new Response();
		response.setMessage("Password reset successful");
		response.setStatus(32);

		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}

	/**
	 * To add profile picture
	 * 
	 * @param token
	 * @param image
	 * @return picture link
	 * @throws FileConversionException
	 */
	@RequestMapping(value = "/addProfilePicture", method = RequestMethod.PUT)
	public ResponseEntity<String> addProfilePicture(@RequestHeader("token") String token,
			@RequestParam MultipartFile image) throws FileConversionException {
		String link = userService.addProfilePicture(token, image);

		return new ResponseEntity<>(link, HttpStatus.OK);
	}

	/**
	 * To set default profile picture
	 * 
	 * @param token
	 * @return picture link
	 */
	@RequestMapping(value = "/removeProfilePicture", method = RequestMethod.PUT)
	public ResponseEntity<Response> removeProfilePicture(@RequestHeader("token") String token) {
		userService.removeProfilePicture(token);

		Response response = new Response();
		response.setMessage("Default profile picture set successfully");
		response.setStatus(33);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * To get profile details
	 * 
	 * @param token
	 * @return
	 */
	@RequestMapping(value = "/getProfileDetails", method = RequestMethod.GET)
	public ResponseEntity<UserProfile> getProfileDetails(@RequestHeader("token") String token) {
		UserProfile userProfile = userService.getProfileDetails(token);

		return new ResponseEntity<>(userProfile, HttpStatus.ACCEPTED);
	}
}
