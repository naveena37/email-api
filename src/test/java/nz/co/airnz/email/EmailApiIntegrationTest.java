/*
 * Copyright 2014-2023 MyWave Limited. All rights reserved.
 */

package nz.co.airnz.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Arrays;
import java.util.List;
import nz.co.airnz.email.model.Email;
import nz.co.airnz.email.model.EmailRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringIntegrationTest
class EmailApiIntegrationTest {

  private static final String API_PATH = "/api/v1/email/account";
  @Autowired
  private MockMvc mockMvc;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    objectMapper.registerModule(new JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  @DisplayName("Return 200 OK when getAllEmails endpoint is invoked")
  @Test
  void testGetAllEmailsSuccess() throws Exception {

    MvcResult response = mockMvc.perform(get(API_PATH.concat("/iDa001")))
        .andExpect(status().isOk()).andReturn();

    List<String> actual = objectMapper.readValue(response.getResponse().getContentAsString(),
        List.class);
    assertTrue(actual.contains("emailRef4"));
  }

  @DisplayName("Return 200 OK when getEmailContent endpoint is invoked")
  @Test
  void testGetEmailContentSuccess() throws Exception {

    MvcResult response = mockMvc.perform(get(API_PATH.concat("/iDa001/emailRef4")))
        .andExpect(status().isOk()).andReturn();

    Email actual = objectMapper.readValue(response.getResponse().getContentAsString(), Email.class);
    assertEquals("Spam: Dangerous goods", actual.getSubject());
  }

  @DisplayName("Return 201 CREATED when sendEmail endpoint is invoked")
  @Test
  void testSendEmailSuccess() throws Exception {
    EmailRequest emailRequest = new EmailRequest(
        "Emergency email",
        null,
        Arrays.asList("abc@airnz.co.nz"),
        null);

    MvcResult response = mockMvc.perform(post(API_PATH.concat("/iDa001"))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(emailRequest)))
        .andExpect(status().isCreated()).andReturn();

    Email actual = objectMapper.readValue(response.getResponse().getContentAsString(), Email.class);
    assertEquals("emailRef-sent", actual.getEmailRef());
    assertEquals("Emergency email", actual.getSubject());
  }

  @DisplayName("Return 201 CREATED when draftEmail endpoint is invoked")
  @Test
  void testDraftEmailSuccess() throws Exception {
    EmailRequest emailRequest = new EmailRequest(
        "Draft email",
        null,
        null,
        null);

    MvcResult response = mockMvc.perform(post(API_PATH.concat("/iDa001/draft"))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(emailRequest)))
        .andExpect(status().isCreated()).andReturn();

    Email actual = objectMapper.readValue(response.getResponse().getContentAsString(), Email.class);
    assertEquals("emailRef-draft", actual.getEmailRef());
    assertEquals("Draft email", actual.getSubject());
  }

  @DisplayName("Return 200 OK when updateEmail endpoint is invoked")
  @Test
  void testUpdateEmailSuccess() throws Exception {
    EmailRequest emailRequest = new EmailRequest(
        "Update the subject",
        "new content",
        Arrays.asList("abc@gmail.com"),
        Arrays.asList("def@gmail.com"));

    MvcResult response = mockMvc.perform(patch(API_PATH.concat("/iDa001/emailRef4/draft"))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(emailRequest)))
        .andExpect(status().isOk()).andReturn();

    Email actual = objectMapper.readValue(response.getResponse().getContentAsString(), Email.class);
    assertEquals("emailRef4", actual.getEmailRef());
    assertEquals("Update the subject", actual.getSubject());
    assertEquals("new content", actual.getContent());
    assertTrue(actual.getToList().contains("abc@gmail.com"));
    assertTrue(actual.getCcList().contains("def@gmail.com"));
  }

  @DisplayName("Return 400 Bad Request when email is incomplete")
  @Test
  void testBadRequestInSendEmailWithNoToList() throws Exception {
    EmailRequest request = new EmailRequest(
        "Some subject", null, null, null); //with no toList

    MvcResult response = mockMvc.perform(post(API_PATH.concat("/iDa001"))
            .contentType("application/json")
            .content(new ObjectMapper().writeValueAsString(request)))
        .andExpect(status().isBadRequest()).andReturn();

    assertThat(response.getResponse().getContentAsString()).contains(
        "Email not complete: toList is mandatory to send an email");
  }

  @DisplayName("Return 400 Bad Request when toList email format is incorrect")
  @Test
  void testBadRequestInUpdateEmailWithIncorrectEmailFormat() throws Exception {
    EmailRequest emailRequest = new EmailRequest(
        "Update the subject",
        "new content",
        Arrays.asList("user#domain.com"),
        null);

    MvcResult response = mockMvc.perform(patch(API_PATH.concat("/iDa001/emailRef4/draft"))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(emailRequest)))
        .andExpect(status().isBadRequest()).andReturn();

    MockHttpServletResponse httpServletResponse = response.getResponse();
    assertEquals(400, httpServletResponse.getStatus());
    assertEquals("Invalid request content.", httpServletResponse.getErrorMessage());
  }

  @DisplayName("Return 400 Bad Request when ccList email format is incorrect")
  @Test
  void testBadRequestInDraftEmailWithIncorrectEmailFormat() throws Exception {
    EmailRequest emailRequest = new EmailRequest(
        "Draft email",
        null,
        null,
        Arrays.asList("@xyz.com"));

    MvcResult response = mockMvc.perform(post(API_PATH.concat("/iDa001/draft"))
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(emailRequest)))
        .andExpect(status().isBadRequest()).andReturn();

    MockHttpServletResponse httpServletResponse = response.getResponse();
    assertEquals(400, httpServletResponse.getStatus());
    assertEquals("Invalid request content.", httpServletResponse.getErrorMessage());
  }
}
