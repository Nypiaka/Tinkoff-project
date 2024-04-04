package edu.java.bot.service.command.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.clients.ScrapperClient;
import edu.java.bot.service.command.Command;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListCommand implements Command {
    private final ScrapperClient scrapperClient;

    @Override
    public String getCommandName() {
        return "/list";
    }

    @Override
    public String getDescription() {
        return "/list - get a list of all available links";
    }

    @Override
    public SendMessage handle(Update update, String[] parts) {
        var links = scrapperClient.getAllLinks(update.message().chat().id()).block();
        return new SendMessage(
            update.message().chat().id(),
            "Tracked links:" + System.lineSeparator() + (links != null
                ? Strings.join(links.getLinks(), '\n') : "")
        );
    }
}
