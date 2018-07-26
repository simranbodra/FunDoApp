package com.bridgelabz.fundoonotes.note.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.bridgelabz.fundoonotes.note.utility.NoteUtility;
import com.bridgelabz.fundoonotes.user.repositories.UserRepository;

@Component
public class NoteInterceptor implements HandlerInterceptor {

	Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());

	@Autowired
	private UserRepository userRepository;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
		log.info("Request URI : " + request.getRequestURI());
		
		String token = request.getHeader("token");
		
		String userId = NoteUtility.parseJWT(token);
		
		if (userRepository.findById(userId).isPresent()) {
			request.setAttribute("token", userId);
			return true;
		}
		return false;
	}

}
