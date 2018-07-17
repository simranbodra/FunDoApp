package com.bridgelabz.fundonotes.usermodule.rabbitmq;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.bridgelabz.fundonotes.usermodule.models.Mail;

@Component
public class ProducerImpl implements MailProducer {

	@Autowired
	private AmqpTemplate rabbitTemplate;
	
	@Value("${javainuse.rabbitmq.exchange}")
	private String exchange;
	
	@Value("${javainuse.rabbitmq.routingkey}")
	private String routingkey;	
	
	@Override
	public void send(Mail mail) {
		System.out.println(exchange);
		System.out.println(mail.getBody() + mail.getTo());
		rabbitTemplate.convertAndSend(exchange, routingkey, mail);
		System.out.println("Send msg = " + mail);
	    
	}
}
