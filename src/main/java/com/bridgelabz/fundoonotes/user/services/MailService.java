package com.bridgelabz.fundoonotes.user.services;

import javax.mail.MessagingException;

import com.bridgelabz.fundoonotes.user.models.Mail;

public interface MailService {

	/**
	 * to send mail 
	 * @param mail contains to, subject and body
	 * @throws MessagingException
	 */
	public void sendMail(Mail mail) throws MessagingException;
}
