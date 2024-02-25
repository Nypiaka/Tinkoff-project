package edu.java.bot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.validator.UrlValidator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BotUtils {
    private static final UrlValidator VALIDATOR = new UrlValidator();

    public static boolean validateLink(String link) {
        return VALIDATOR.isValid(link);
    }
}
