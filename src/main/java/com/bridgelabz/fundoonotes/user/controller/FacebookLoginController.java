package com.bridgelabz.fundoonotes.user.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.fundoonotes.user.models.Response;
import com.bridgelabz.fundoonotes.user.services.FacebookService;

@RestController
public class FacebookLoginController {

	@Autowired
	private FacebookService facebookService;

	@RequestMapping(value = "/createFacebookAuthorization", method = RequestMethod.GET)
	public String createFacebookAuthorization() {
		return facebookService.createFacebookAuthorizationURL();
	}

	@RequestMapping(value = "/facebook", method = RequestMethod.GET)
	public ResponseEntity<Response> createFacebookAccessToken(@RequestParam String code, HttpServletResponse resp) {
		String token = facebookService.createFacebookAccessToken(code);

		resp.setHeader("token", token);

		Response response = new Response();
		response.setMessage("Login Successful");
		response.setStatus(20);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
