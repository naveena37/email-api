package nz.co.airnz.email.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.common.util.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import nz.co.airnz.email.model.Email;
import nz.co.airnz.email.model.EmailRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class EmailServiceImpl implements EmailService {

  private final Map<String, Map<String, Email>> accounts = new LinkedHashMap<>();

  private final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

  public EmailServiceImpl() {
    objectMapper.registerModule(new JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    loadEmailsAtStartUp();
  }

  private void loadEmailsAtStartUp() {
    try {
      // Adding three emails under pJo001
      Map<String, Email> account1_emails = new LinkedHashMap<>();
      Email email1 = objectMapper.readValue(
          getResource("/emails/pJo001/emailRef1.json"), Email.class);
      account1_emails.put(email1.getEmailRef(), email1);
      Email email2 = objectMapper.readValue(
          getResource("/emails/pJo001/emailRef2.json"), Email.class);
      account1_emails.put(email2.getEmailRef(), email2);
      Email email3 = objectMapper.readValue(
          getResource("/emails/pJo001/emailRef3.json"), Email.class);
      account1_emails.put(email3.getEmailRef(), email3);

      accounts.put("pJo001", account1_emails);
      log.info("Loaded " + account1_emails.size() + " emails in memory for pJo001");

      // Adding two emails under iDa001
      Map<String, Email> account2_emails = new LinkedHashMap<>();
      Email email4 = objectMapper.readValue(
          getResource("/emails/iDa001/emailRef4.json"), Email.class);
      account2_emails.put(email4.getEmailRef(), email4);
      Email email5 = objectMapper.readValue(
          getResource("/emails/iDa001/emailRef5.json"), Email.class);
      account2_emails.put(email5.getEmailRef(), email5);

      accounts.put("iDa001", account2_emails);
      log.info("Loaded " + account2_emails.size() + " emails in memory for iDa001");

    } catch (IOException e) {
      log.error("JSON config data is incorrect - load failed");
    }
  }

  private InputStream getResource(String fileName) {
    return this.getClass().getResourceAsStream(fileName);
  }

  @Override
  public ResponseEntity getAllEmails(String accountName) {
    if (!isAccountFound(accountName)) {
      return getNoAccountResponse(accountName);
    }

    // Only returning the email References - it would be good if the dates are also returned
    return new ResponseEntity(new ArrayList<>(accounts.get(accountName).keySet()), HttpStatus.OK);
  }

  @Override
  public ResponseEntity getEmailContent(String accountName, String emailRef) {

    if (!isAccountFound(accountName)) {
      return getNoAccountResponse(accountName);
    }

    Map<String, Email> emails = accounts.get(accountName);

    if (emails.containsKey(emailRef)) {
      return new ResponseEntity(emails.get(emailRef), HttpStatus.OK);
    }

    log.error("Email reference number {} not found", emailRef);
    return new ResponseEntity("Email reference number not found", HttpStatus.NOT_FOUND);
  }

  @Override
  public ResponseEntity draftEmail(String accountName, EmailRequest emailRequest) {
    if (!isAccountFound(accountName)) {
      return getNoAccountResponse(accountName);
    }

    Email email = new Email(
        "emailRef-draft",
        emailRequest.subject(),
        ZonedDateTime.now(),
        null,
        emailRequest.content(),
        emailRequest.toList(),
        emailRequest.ccList()
    );

    try {
      String emailString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(email);
      log.info("Here is your draft email:" + emailString);

      Map<String, Email> emails = accounts.get(accountName);
      emails.put(email.getEmailRef(), email);

      return new ResponseEntity(email, HttpStatus.CREATED);
    } catch (IOException e) {
      return new ResponseEntity(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  @Override
  public ResponseEntity sendEmail(String accountName, EmailRequest emailRequest) {

    if (!isAccountFound(accountName)) {
      return getNoAccountResponse(accountName);
    }

    /*
    We check for email completeness only while sending the email
    In our implementation, we assume it is ok to send email without subject, content or cc List.
    Only toList is mandatory similar to a typical email service requirement.
    */
    String emailCheck = checkEmailCompleteness(emailRequest);
    if(emailCheck != null) {
      return getBadRequestResponse(emailCheck);
    }

    Email email = new Email(
        "emailRef-sent",
        emailRequest.subject(),
        ZonedDateTime.now(),
        null,
        emailRequest.content(),
        emailRequest.toList(),
        emailRequest.ccList()
    );

    try {
      String emailString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(email);
      log.info("Here is the preview of your email:" + emailString);

      // We assume there is an integration with email server and the relevant email is created and sent

      Map<String, Email> emails = accounts.get(accountName);
      emails.put(email.getEmailRef(), email);

      return new ResponseEntity(email, HttpStatus.CREATED);
    } catch (IOException e) {
      return new ResponseEntity(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  @Override
  public ResponseEntity updateEmail(String accountName, String emailRef,
      EmailRequest updateRequest) {

    if (!isAccountFound(accountName)) {
      return getNoAccountResponse(accountName);
    }

    Map<String, Email> emails = accounts.get(accountName);
    if (emails.containsKey(emailRef)) {
      Email email = emails.get(emailRef);
      try {

        if (StringUtils.isNotEmpty(updateRequest.subject())) {
          email.setSubject(updateRequest.subject());
        }
        if (StringUtils.isNotEmpty(updateRequest.content())) {
          email.setContent(updateRequest.content());
        }
        if (!CollectionUtils.isEmpty(updateRequest.toList())) {
          email.setToList(updateRequest.toList());
        }
        if (!CollectionUtils.isEmpty(updateRequest.ccList())) {
          email.setCcList(updateRequest.ccList());
        }

        String emailString = objectMapper.writerWithDefaultPrettyPrinter()
            .writeValueAsString(email);
        log.info("Here is your updated email:" + emailString);

        return new ResponseEntity(email, HttpStatus.OK);
      } catch (IOException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
      }
    }
    return new ResponseEntity("Email reference not found", HttpStatus.NOT_FOUND);
  }

  private boolean isAccountFound(String accountName) {
    return accounts.containsKey(accountName);
  }

  private ResponseEntity getNoAccountResponse(String name) {
    log.error("Email account {} not found", name);
    return new ResponseEntity("Email account not found", HttpStatus.NOT_FOUND);
  }

  private String checkEmailCompleteness(EmailRequest emailRequest) {
    if(CollectionUtils.isEmpty(emailRequest.toList())) {
      return "Email not complete: toList is mandatory to send an email";
    }
    return null;
  }

  private ResponseEntity getBadRequestResponse(String message) {
    log.error(message);
    return new ResponseEntity(message, HttpStatus.BAD_REQUEST);
  }
}
