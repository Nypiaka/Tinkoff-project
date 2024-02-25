package edu.java.dto.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OwnerDto {
    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("link")
    private String link;
}
