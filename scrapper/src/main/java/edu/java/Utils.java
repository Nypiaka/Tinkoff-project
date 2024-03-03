package edu.java;

import edu.java.dto.handlers.ApiErrorResponse;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

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
