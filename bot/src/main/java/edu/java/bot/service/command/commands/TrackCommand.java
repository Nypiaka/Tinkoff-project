package edu.java.bot.service.command.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.service.command.Command;
import edu.java.utils.Utils;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class TrackCommand implements Command {

    @Autowired
    private ScrapperClient scrapperClient;

    @Override
    public String getCommandName() {
        return "/track";
    }

    @Override
    public String getDescription() {
        return "/track {link} - start tracking link";
    }

    @Override
    public SendMessage handle(Update update, String[] parts) {
        String ans;
        if (parts.length < 2) {
            ans = "Please, insert link to track in format \"/track {link}\".";
        } else {
            if (Utils.validateLink(parts[1])) {
                try {
                    scrapperClient.addLink(update.message().chat().id(), URI.create(parts[1])).block();
                    ans = "Link added successful.";
                } catch (WebClientResponseException e) {
                    ans = "Oops! Link was not added.";
                }
            } else {
                ans = "Wrong link format!";
            }
        }
        return new SendMessage(update.message().chat().id(), ans);
    }
}
