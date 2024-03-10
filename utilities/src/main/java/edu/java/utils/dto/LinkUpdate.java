package edu.java.utils.dto;

import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LinkUpdate {
    private long id;

    private URI url;

    private String description;

    private List<Long> tgChatIds;

}
