package edu.java.utils.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    private String type;

    private String code;

    private String exceptionName;

    private String exceptionMessage;

    private List<String> stacktrace;
}
