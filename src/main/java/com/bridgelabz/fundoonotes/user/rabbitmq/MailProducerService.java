package com.bridgelabz.fundoonotes.user.rabbitmq;

import com.bridgelabz.fundoonotes.user.models.Mail;

public interface MailProducerService {

	/***
	 * To send mail to queue
	 * @param mail contains to, subject and body
	 */
	public void send(Mail mail);

}
