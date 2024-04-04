package edu.java.utils.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    @JsonProperty("type")
    private String type;
    @JsonProperty("code")
    private String code;
    @JsonProperty("exception-name")
    private String exceptionName;
    @JsonProperty("exception-message")
    private String exceptionMessage;
    @JsonProperty("stacktrace")
    private List<String> stacktrace;
}
