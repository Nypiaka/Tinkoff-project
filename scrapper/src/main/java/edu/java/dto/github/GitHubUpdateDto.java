package edu.java.dto.github;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class GitHubUpdateDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("before")
    private String before;
    @JsonProperty("after")
    private String after;
    @JsonProperty("timestamp")
    private OffsetDateTime timeStamp;
    @JsonProperty("activity_type")
    private String activityType;
    @JsonProperty("actor")
    private UserDto actor;
}
