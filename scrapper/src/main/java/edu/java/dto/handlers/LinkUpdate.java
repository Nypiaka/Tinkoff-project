package edu.java.dto.handlers;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkUpdate {
    @JsonProperty("id")
    long id;

    @JsonProperty("url")
    URI url;

    @JsonProperty("description")
    String description;

    @JsonProperty("tgChatIds")
    List<Long> tgChatIds;

}
