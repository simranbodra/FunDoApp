package com.bridgelabz.fundonotes.usermodule.services;

import javax.mail.MessagingException;

import com.bridgelabz.fundonotes.usermodule.models.Mail;

public interface MailService {

	public void sendActivationLink(Mail mail) throws MessagingException;
}
