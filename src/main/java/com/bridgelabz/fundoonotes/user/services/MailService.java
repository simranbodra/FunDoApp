package com.bridgelabz.fundoonotes.user.services;

import javax.mail.MessagingException;

import com.bridgelabz.fundoonotes.user.models.Mail;

public interface MailService {

	public void sendMail(Mail mail) throws MessagingException;
}
