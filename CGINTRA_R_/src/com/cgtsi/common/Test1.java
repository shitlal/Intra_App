package com.cgtsi.common;
import java.util.ArrayList;
import java.util.Properties;
import com.cgtsi.common.Mailer;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.cgtsi.admin.Message;
import com.cgtsi.common.Log;
import com.cgtsi.common.MailerException;
import com.cgtsi.common.Mailer.SMTPAuthenticator;
import com.cgtsi.util.PropertyLoader;

public class Test1 {
 
	//##
	public class SMTPAuthenticator extends javax.mail.Authenticator
	{
		private String userId="";
		private String password="";

		public SMTPAuthenticator(String userId, String password)
		{
			this.userId=userId;
			this.password=password;
		}

		public PasswordAuthentication getPasswordAuthentication()
		{
			return new PasswordAuthentication(userId, password);
		}
	}
	//##
	
	 public static void main() throws MailerException {	
	boolean sendMailVal=false;
	//get the user id, password, and smtp host names;
	String userId="";
	String password="";
	String smtpHost="";
	Session session = null;
	
	try
	{
		System.out.println("Raja");
		Properties props = new Properties();
		props.put("mail.smtp.host","202.138.96.11");		
		props.put("mail.smtp.auth", "true");			
		Authenticator auth = new Test1(). new SMTPAuthenticator("raju.konkati@cgtmse.in" ,"cgt$2018");
		session = Session.getDefaultInstance(props, auth);
		session.setDebug(false);	
		javax.mail.Message msg = new MimeMessage(session);
		InternetAddress addressFrom = new InternetAddress("administrator@cgtmse.in");		
		msg.setFrom(addressFrom);		
		//ArrayList to=message.getTo();
		ArrayList to = new ArrayList();
		to.add("raju.konkati@cgtmse.in");		
        // System.out.println("To Email Id:"+to);
         //   to.add("it@cgtmse.com");		
		//Transport transport = session.getTransport("smtp"); 
		//transport.connect("mail.smtp.host","rt14509","$Hrek1");
		
		//This mail integrated only for To Addresses. If required, CC, BCC can be implemented.
		for(int i=0;i<to.size();i++)
		{
			try
			{
			InternetAddress[] addressTo = new InternetAddress[1];
			addressTo[0]=new InternetAddress((String)to.get(i));
			//Log.log(Log.DEBUG,"Mailer","sendEmail","to "+addressTo[0]);
            System.out.println("To Mail Ids raju mail :"+addressTo[0]);    
			msg.setRecipients(javax.mail.Message.RecipientType.TO, addressTo);   
    
            msg.setSubject("Test Mail Message from test server subject");
			msg.setContent("Test Mail Message from test server content", "text/plain");			
			System.out.println("loop no"+i); 
			Transport.send(msg);
			}
		
			catch(AddressException invalidAddress)
					{
						System.out.println("inside invalid address");
						throw new MailerException("sending E-mail failed. Invalid E-Mail address");
			
					}

			catch (SendFailedException sfe) 
			{
				System.out.println("PATH inside send failed");
				
				throw new MailerException("sending E-mail failed.");
			}

			catch(AuthenticationFailedException AuthenticationFailed)
						{
							System.out.println("inside authenticatiobn failed");
							throw new MailerException("sending E-mail failed.");
						}

			
			catch (MessagingException me)
							{
								System.out.println("inside messaging exception");
								throw new MailerException("sending E-mail failed.");
							}
							
			  }
		
	}

catch(Exception e)
		{
			Log.log(Log.ERROR,"Mailer","sendEmail",e.getMessage());

			throw new MailerException("sending E-mail failed.");
		}
 }
}    
    
    
    
