package nz.co.airnz.email.service;


import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Arrays;
import nz.co.airnz.email.model.EmailRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class EmailServiceImplTest {

  EmailServiceImpl service = new EmailServiceImpl();

  @BeforeEach
  void setUp() {

  }

  @Test
  void getAllEmails() {
    ResponseEntity actual1 = service.getAllEmails("iDa001");
    assertSame(HttpStatus.OK, actual1.getStatusCode());

    ResponseEntity actual2 = service.getAllEmails("iDa002");
    assertSame(HttpStatus.NOT_FOUND, actual2.getStatusCode());
  }

  @Test
  void getEmailContent() {
    ResponseEntity actual1 = service.getEmailContent("iDa001", "emailRef4");
    assertSame(HttpStatus.OK, actual1.getStatusCode());

    ResponseEntity actual2 = service.getEmailContent("iDa001", "emailRef1");
    assertSame(HttpStatus.NOT_FOUND, actual2.getStatusCode());
  }

  @Test
  void draftEmail() {
    EmailRequest emailRequest = new EmailRequest(
        "Brand new draft",
        null,
        null,
        null);
    ResponseEntity actual1 = service.draftEmail("iDa001", emailRequest);
    assertSame(HttpStatus.CREATED, actual1.getStatusCode());

    ResponseEntity actual2 = service.draftEmail("iDa002", emailRequest);
    assertSame(HttpStatus.NOT_FOUND, actual2.getStatusCode());
  }

  @Test
  void sendEmail() {
    EmailRequest emailRequest = new EmailRequest(
        "Emergency email",
        "Since we specify the response status programmatically, we can return with different status codes for different scenarios",
        Arrays.asList("abc@gmail.com"),
        null);
    ResponseEntity actual1 = service.sendEmail("iDa001", emailRequest);
    assertSame(HttpStatus.CREATED, actual1.getStatusCode());

    ResponseEntity actual2 = service.sendEmail("iDa002", emailRequest);
    assertSame(HttpStatus.NOT_FOUND, actual2.getStatusCode());
  }

  @Test
  void updateEmail() {
    EmailRequest updateRequest = new EmailRequest(
        "Updated subject",
        null,
        null,
        null);
    ResponseEntity actual1 = service.updateEmail("iDa001", "emailRef4", updateRequest);
    assertSame(HttpStatus.OK, actual1.getStatusCode());

    ResponseEntity actual2 = service.updateEmail("iDa002", "emailRef1", updateRequest);
    assertSame(HttpStatus.NOT_FOUND, actual2.getStatusCode());
  }
}