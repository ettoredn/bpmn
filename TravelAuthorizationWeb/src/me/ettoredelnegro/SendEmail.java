package me.ettoredelnegro;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * Singleton. Only one instance of the delegate is created.
 * The execute method is called for each service task using the delegate.
 * 
 * See http://www.activiti.org/userguide/#bpmnJavaServiceTask
 * @author ettore
 */
public class SendEmail implements JavaDelegate
{
	// Lazy injected by ActivitiDeployment
	public static String smtpUsername;
	public static String smtpPassword;
	public static String smtpHost;
	public static Integer smtpPort;
	public static String defaultFrom;
	public static boolean useTLS;
	public static Properties smtpProperties;
	
	// Injected
	private Expression mailSubject;
	private Expression mailText;
	private Expression mailRecipient;
	private Expression mailSender;
	
	public SendEmail()
	{
		smtpProperties = new Properties();
		smtpProperties.put("mail.smtp.auth", "true");
		smtpProperties.put("mail.smtp.starttls.enable", ""+ useTLS);
		smtpProperties.put("mail.smtp.host", smtpHost);
		smtpProperties.put("mail.smtp.port", ""+ smtpPort);
	}
		
	@Override
	public void execute(DelegateExecution execution) throws Exception
	{
		Session session = Session.getInstance(smtpProperties,
				new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(smtpUsername, smtpPassword);
			}
		});
		String fromString = mailSender != null ? (String) mailSender.getValue(execution) : defaultFrom;
		String recipientString = mailRecipient != null ? (String) mailRecipient.getValue(execution) : "ete.dne@gmail.com";
		String mailTextString = (String) mailText.getValue(execution);
		String subjectString = (String) mailSubject.getValue(execution);
		
		try {
			InternetAddress[] recipients = InternetAddress.parse(recipientString);
			
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromString, true));
			message.setRecipients(Message.RecipientType.TO, recipients);
			message.setSubject(subjectString);
			message.setText(mailTextString);
			
			Transport.send(message);
		} catch (AddressException e) {
			System.out.println("[delegate.SendEmail] Invalid mail address "+ (String) mailRecipient.getValue(execution));
			throw new BpmnError("mailError");
		} catch (MessagingException e) {
			System.out.println("[delegate.SendMail] "+ e.getMessage());
			throw new BpmnError("mailError");
		}
		
		System.out.println("Mail from "+ fromString +" sent to "+ recipientString +" with text: "+ mailTextString);
	}
}
