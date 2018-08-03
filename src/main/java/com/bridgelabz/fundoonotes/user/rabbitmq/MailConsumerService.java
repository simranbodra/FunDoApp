package com.bridgelabz.fundoonotes.user.rabbitmq;

import javax.mail.MessagingException;

import com.bridgelabz.fundoonotes.user.models.Mail;

public interface MailConsumerService {

	/**
	 * TO receive the mail from the queue
	 * @param mail
	 * @throws MessagingException
	 */
	public void receive(Mail mail) throws MessagingException;
		
}
