package nz.co.airnz.email.service;

import nz.co.airnz.email.model.EmailRequest;
import org.springframework.http.ResponseEntity;

public interface EmailService {

  ResponseEntity getAllEmails(String accountName);

  ResponseEntity getEmailContent(String accountName, String emailRef);

  ResponseEntity draftEmail(String accountName, EmailRequest emailRequest);

  ResponseEntity sendEmail(String accountName, EmailRequest emailRequest);

  ResponseEntity updateEmail(String accountName, String emailRef, EmailRequest updateRequest);
}
