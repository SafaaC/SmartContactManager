package com.smart.service;

//import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
/*import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;*/

@Service
public class EmailService {
	@Autowired
	private JavaMailSender mailSender;
	
	public boolean sendEmail(String subject, String message, String to) {
		boolean f = false;
		String from = "smartcontactmanagerproject@gmail.com";
		
		
	SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		try {
			// from mail
			simpleMailMessage.setFrom(from);
	
			// recipient mail
			simpleMailMessage.setTo(to);;
	
			// adding subject
			simpleMailMessage.setSubject(subject);
	
			// adding text content
			simpleMailMessage.setText(message);
			
			mailSender.send(simpleMailMessage);
			
			System.out.println("Sent Success");
			f=true;
			} catch (Exception e) {
			e.printStackTrace();
		}
	return f;

	}
	
	
	
	
	
	
	
	// Email Web Api(java Mail)
/*	

		// This is responsible to send email
		public boolean sendEmail(String subject, String message, String to) {
			
			boolean f = false;
			
			String from = "smartcontactmanagerproject@gmail.com";
			
			// Variable for gmail
			String host = "smtp.gmail.com";

			// get system properties
			Properties properties = System.getProperties();

			// setting important information to properties object

			// host set
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", "465");
			properties.put("mail.smtp.ssl.enable", "true");
			properties.put("mail.smtp.auth", "true");

			// step 1 : To get the Session Object
			Session session = Session.getInstance(properties, new Authenticator() {

				@Override
				protected PasswordAuthentication getPasswordAuthentication() {

					return new PasswordAuthentication("smartcontactmanagerproject@gmail.com", "oygxclxfzhwgfcqi");
				}

			});
			session.setDebug(true);

			// compose the message [text,multimedia]
			MimeMessage mimeMessage = new MimeMessage(session);
			try {
				// from mail
				mimeMessage.setFrom(from);

				// recipient mail
				mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

				// adding subject
				mimeMessage.setSubject(subject);

				// adding text content
				mimeMessage.setText(message);

				// send
				// Step 3: send the message using Transport class
				Transport.send(mimeMessage);

				System.out.println("Sent Success");
				f=true;
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			return f;
		
	}*/
}
