package edu.java.bot;

import edu.java.bot.dto.ApiErrorResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.validator.UrlValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BotUtils {
    private static final UrlValidator VALIDATOR = new UrlValidator();

    public static boolean validateLink(String link) {
        return VALIDATOR.isValid(link);
    }

    public static ResponseEntity<ApiErrorResponse> errorRequest(int code) {
        var message = HttpStatus.valueOf(code).getReasonPhrase();
        var resp = ResponseEntity.status(code);
        return resp.body(new ApiErrorResponse(
            message,
            String.valueOf(code),
            message,
            message,
            List.of()
        ));
    }

}
