package edu.java.dto.stackoverflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class StackOverflowUpdateDto {
    @JsonProperty("creation_date")
    private OffsetDateTime creationDate;

    @JsonProperty("timeline_type")
    private String timelineType;

    @JsonProperty("post_id")
    private Long postId;

    @JsonProperty("owner")
    private OwnerDto owner;

    @Override
    public String toString() {
        return String.format(
            """
                New updates!
                Update time: %s,
                Update type: %s,
                Update actor: %s
                """, creationDate, timelineType, owner.getLink()
        );
    }
}
