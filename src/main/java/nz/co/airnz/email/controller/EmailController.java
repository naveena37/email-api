package nz.co.airnz.email.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import nz.co.airnz.email.model.Email;
import nz.co.airnz.email.model.EmailRequest;
import nz.co.airnz.email.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/email")
@Tag(name = "Email API", description = "Email API")
public class EmailController {

  private final EmailService emailService;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private static final Logger log = LoggerFactory.getLogger(EmailController.class);

  public EmailController(@Autowired EmailService emailService) {
    this.emailService = emailService;
    objectMapper.registerModule(new JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
  }

  @Operation(
      summary = "Retrieve the contents of the user's inbox (Please use account name pJo001).",
      description = "Retrieve the contents of the user's inbox.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", content = {
          @Content(schema = @Schema(implementation = List.class), mediaType = "application/json")}),
      @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})})
  @GetMapping("/account/{name}")
  public ResponseEntity getAllEmails(@PathVariable String name) {
    log.info("getAllEmails endpoint with account name {}", name);
    return emailService.getAllEmails(name);
  }

  @Operation(
      summary = "Retrieve the contents of a single email. (Please use account name pJo001 and emailRef1)",
      description = "Retrieve the contents of a single email.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", content = {
          @Content(schema = @Schema(implementation = Email.class), mediaType = "application/json")}),
      @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})})
  @GetMapping("/account/{name}/{emailRef}")
  public ResponseEntity getEmailContent(@PathVariable String name, @PathVariable String emailRef) {
    log.info("getEmailContent endpoint with account name {} and emailRef {}", name, emailRef);
    return emailService.getEmailContent(name, emailRef);
  }

  @Operation(
      summary = "Write a draft email and save it for later.",
      description = "Write a draft email and save it for later.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", content = {
          @Content(schema = @Schema(implementation = Email.class), mediaType = "application/json")}),
      @ApiResponse(responseCode = "400", content = {
          @Content(schema = @Schema(implementation = ResponseEntity.class), mediaType = "application/json")}),
      @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
      @ApiResponse(responseCode = "422", content = {
          @Content(schema = @Schema(implementation = ResponseEntity.class), mediaType = "application/json")})})
  @PostMapping("/account/{name}/draft")
  public ResponseEntity draftEmail(@PathVariable String name,
      @Valid @RequestBody EmailRequest emailRequest) {
    log.info("draftEmail endpoint with account name {} and email request", name);
    return emailService.draftEmail(name, emailRequest);
  }

  @Operation(
      summary = "Send an email.",
      description = "Send an email.")
  @ApiResponses({
      @ApiResponse(responseCode = "201", content = {
          @Content(schema = @Schema(implementation = Email.class), mediaType = "application/json")}),
      @ApiResponse(responseCode = "400", content = {
          @Content(schema = @Schema(implementation = ResponseEntity.class), mediaType = "application/json")}),
      @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
      @ApiResponse(responseCode = "422", content = {
          @Content(schema = @Schema(implementation = ResponseEntity.class), mediaType = "application/json")})})
  @PostMapping("/account/{name}")
  public ResponseEntity sendEmail(@PathVariable String name,
      @Valid @RequestBody EmailRequest emailRequest) {
    log.info("sendEmail endpoint with account name {} and email request", name);
    return emailService.sendEmail(name, emailRequest);
  }

  @Operation(
      summary = "Update one or more properties of draft email.",
      description = "Update one or more properties of draft email.")
  @ApiResponses({
      @ApiResponse(responseCode = "200", content = {
          @Content(schema = @Schema(implementation = Email.class), mediaType = "application/json")}),
      @ApiResponse(responseCode = "400", content = {
          @Content(schema = @Schema(implementation = ResponseEntity.class), mediaType = "application/json")}),
      @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
      @ApiResponse(responseCode = "422", content = {
          @Content(schema = @Schema(implementation = ResponseEntity.class), mediaType = "application/json")})})
  @PatchMapping("/account/{name}/{emailRef}/draft")
  public ResponseEntity updateEmail(@PathVariable String name, @PathVariable String emailRef,
      @Valid @RequestBody EmailRequest updateRequest) {
    log.info("updateEmail endpoint with account name {} and emailRef {}", name, emailRef);
    return emailService.updateEmail(name, emailRef, updateRequest);
  }

}
