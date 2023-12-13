package nz.co.airnz.email.model;

import jakarta.validation.constraints.Pattern;
import java.util.List;

public record EmailRequest(String subject,
                           String content,
                           List<@Pattern(regexp = "^(.+)@(.+)$") String> toList,
                           List<@Pattern(regexp = "^(.+)@(.+)$") String> ccList) { }
