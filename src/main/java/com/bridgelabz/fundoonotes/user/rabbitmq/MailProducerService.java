package com.bridgelabz.fundoonotes.user.rabbitmq;

import com.bridgelabz.fundoonotes.user.models.Mail;

public interface MailProducerService {

	public void send(Mail mail);

}
