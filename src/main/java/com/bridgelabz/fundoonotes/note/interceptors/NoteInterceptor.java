package com.bridgelabz.fundoonotes.note.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.bridgelabz.fundoonotes.user.repositories.UserRepository;
import com.bridgelabz.fundoonotes.user.utility.JWTokenProvider;

@Component
public class NoteInterceptor implements HandlerInterceptor {

	Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JWTokenProvider tokenProvider;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
		log.info("Request URI : " + request.getRequestURI());
		
		String token = request.getHeader("Authorization");
		
		String userId = tokenProvider.parseJWT(token);
		
		if (userRepository.findById(userId).isPresent()) {
			request.setAttribute("UserId", userId);
			return true;
		}
		return false;
	}

}
