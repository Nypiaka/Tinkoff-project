package edu.java.dto.handlers;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    String type;

    String code;

    String exceptionName;

    String exceptionMessage;

    List<String> stacktrace;
}
