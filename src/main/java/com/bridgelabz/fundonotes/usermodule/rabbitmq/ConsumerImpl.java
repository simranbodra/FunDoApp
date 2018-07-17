package com.bridgelabz.fundonotes.usermodule.rabbitmq;

import javax.mail.MessagingException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bridgelabz.fundonotes.usermodule.models.Mail;
import com.bridgelabz.fundonotes.usermodule.services.MailService;

@Component
public class ConsumerImpl implements MailConsumer {
	
	Mail mail;
	
	@Autowired
	private MailService mailService;
	
	@Override
	@RabbitListener(queues="javainuse.queue")
	public void receive(Mail mail) throws MessagingException {
		System.out.println("Receive msg = "+ mail.toString());
		this.mail = mail;
		
		mailService.sendLink(mail);
		
	}
	
	@Override
	public Mail receiveMail() {
		System.out.println(this.mail);
		 return this.mail;
	 }
}
