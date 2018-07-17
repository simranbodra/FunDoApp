package com.bridgelabz.fundonotes.usermodule.rabbitmq;

import com.bridgelabz.fundonotes.usermodule.models.Mail;

public interface MailProducer {

	public void send(Mail mail);

}
