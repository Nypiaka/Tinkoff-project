package edu.java.bot;

import edu.java.bot.dto.ApiErrorResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.validator.UrlValidator;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BotUtils {
    private static final UrlValidator VALIDATOR = new UrlValidator();

    public static boolean validateLink(String link) {
        return VALIDATOR.isValid(link);
    }

    public static ResponseEntity<ApiErrorResponse> errorRequest(int code) {
        var message = "";
        switch (code) {
            case 400 -> message = "bad request";
            case 404 -> message = "not found";
            default -> message = "";
        }
        return ResponseEntity.status(code).body(new ApiErrorResponse(
            message,
            String.valueOf(code),
            message,
            message,
            List.of()
        ));
    }

}
