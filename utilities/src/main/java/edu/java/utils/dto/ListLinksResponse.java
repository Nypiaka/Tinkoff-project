package edu.java.utils.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListLinksResponse {

    private List<LinkResponse> links;

    private int size;

}
