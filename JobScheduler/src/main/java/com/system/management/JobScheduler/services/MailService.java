package com.system.management.JobScheduler.services;

import java.nio.charset.StandardCharsets;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.system.management.JobScheduler.commons.Constants;

@Service
public class MailService {
	
	private static Logger LOGGER = LoggerFactory.getLogger(MailService.class);
	
	@Autowired
	private JavaMailSender mailSender ;
	
	public void sendMail(String toEmail , String subject , String body) {

		try {
			LOGGER.info("Sending Email to {}", toEmail);
			MimeMessage message = mailSender.createMimeMessage();

			MimeMessageHelper messageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.toString());
			messageHelper.setSubject(subject);
			messageHelper.setText(body, true);
			messageHelper.setFrom(Constants.FROM_EMAIL);
			messageHelper.setTo(toEmail);

			mailSender.send(message);
		} catch (Exception ex) {
			LOGGER.error("Failed to send email to {}", toEmail);
		}
	
		
	}
}
