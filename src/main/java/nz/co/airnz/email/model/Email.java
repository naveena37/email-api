package nz.co.airnz.email.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.time.ZonedDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Email {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String emailRef;
    private String subject;
    private final ZonedDateTime date;
    private final String sender;
    private String content;
    private List<String> toList;
    private List<String> ccList;

    @JsonCreator
    public Email(
        @JsonProperty("emailRef") String emailRef,
        @JsonProperty("subject") String subject,
        @JsonProperty("date") ZonedDateTime date,
        @JsonProperty("sender") String sender,
        @JsonProperty("content") String content,
        @JsonProperty("toList") List<String> toList,
        @JsonProperty("ccList") List<String> ccList) {
        super();
        this.emailRef = emailRef;
        this.subject = subject;
        this.date = date;
        this.sender = sender;
        this.content = content;
        this.toList = toList;
        this.ccList = ccList;
    }

    public String getEmailRef() {
        return emailRef;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public List<String> getToList() {
        return toList;
    }

    public List<String> getCcList() {
        return ccList;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setToList(List<String> toList) {
        this.toList = toList;
    }

    public void setCcList(List<String> ccList) {
        this.ccList = ccList;
    }
}
