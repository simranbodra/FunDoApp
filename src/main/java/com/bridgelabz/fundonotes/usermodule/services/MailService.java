package com.bridgelabz.fundonotes.usermodule.services;

import javax.mail.MessagingException;

import com.bridgelabz.fundonotes.usermodule.models.Mail;

public interface MailService {

	public void sendLink(Mail mail) throws MessagingException;
}
