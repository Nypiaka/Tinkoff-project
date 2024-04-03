package edu.java.bot.service.command.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.service.command.Command;
import edu.java.utils.Utils;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class UntrackCommand extends AbstractTrackingCommand implements Command {
    private final ScrapperClient scrapperClient;

    @Override
    public String getCommandName() {
        return "/untrack";
    }

    @Override
    public String getDescription() {
        return "/untrack {link} - stop tracking link";
    }

    @Override
    public SendMessage handle(Update update, String[] parts) {
        var content = update.message().text().split(" ");
        String ans;
        if (content.length < 2) {
            ans = wrongLinkFormatDescription();
        } else {
            if (Utils.validateLink(parts[1])) {
                try {
                    scrapperClient.removeLink(update.message().chat().id(), URI.create(parts[1])).block();
                    ans = actionWithLinkSuccessful();
                } catch (WebClientResponseException e) {
                    ans = actionWithLinkUnSuccessful();
                }
            } else {
                ans = wrongLinkFormat();
            }
        }
        return new SendMessage(update.message().chat().id(), ans);
    }

    @Override
    protected String getAction() {
        return "removed";
    }
}
