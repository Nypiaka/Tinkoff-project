package edu.java.utils;

import edu.java.utils.dto.ApiErrorResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.validator.UrlValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    private static final UrlValidator VALIDATOR = new UrlValidator();

    private static final String GITHUB_IDENTIFIER = "github.com";

    public static final String STACKOVERFLOW_IDENTIFIER = "questions";

    public static boolean isGitHubLink(String link) {
        return link.contains("github");
    }

    public static boolean isStackOverflowLink(String link) {
        return link.contains("stackoverflow");
    }

    private static String extractLinkToUri(String link, String identifier) {
        String[] parts = link.split("/");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals(identifier)) {
                return switch (identifier) {
                    case GITHUB_IDENTIFIER -> parts[i + 1] + "/" + parts[i + 2] + "/activity";
                    case STACKOVERFLOW_IDENTIFIER -> parts[i + 1] + "/timeline?site=stackoverflow";
                    default -> throw new IllegalStateException("Unexpected value: " + identifier);
                };
            }
        }
        return null;
    }

    public static String stackOverflowLinkToUri(String link) {
        return extractLinkToUri(link, STACKOVERFLOW_IDENTIFIER);
    }

    public static String githubLinkToUri(String link) {
        return extractLinkToUri(link, GITHUB_IDENTIFIER);
    }

    public static ResponseEntity<ApiErrorResponse> errorRequest(int code) {
        var message = HttpStatus.valueOf(code).getReasonPhrase();
        return ResponseEntity.status(code).body(new ApiErrorResponse(
            message,
            String.valueOf(code),
            message,
            message,
            List.of()
        ));
    }

    public static boolean validateLink(String link) {
        return VALIDATOR.isValid(link);
    }
}
