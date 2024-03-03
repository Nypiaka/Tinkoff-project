package edu.java.dto.handlers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.net.URI;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoveLinkRequest {
    URI link;
}
