package com.bridgelabz.fundoonotes.user.rabbitmq;

import javax.mail.MessagingException;

import com.bridgelabz.fundoonotes.user.models.Mail;

public interface MailConsumerService {

	public void receive(Mail mail) throws MessagingException;
		
}
