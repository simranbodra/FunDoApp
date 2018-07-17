package com.bridgelabz.fundonotes.usermodule.rabbitmq;

import javax.mail.MessagingException;

import com.bridgelabz.fundonotes.usermodule.models.Mail;

public interface MailConsumer {

	public void receive(Mail mail) throws MessagingException;
	
	public Mail receiveMail() ;
	
}
