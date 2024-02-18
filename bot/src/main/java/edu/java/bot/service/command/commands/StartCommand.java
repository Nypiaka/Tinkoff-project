package edu.java.bot.service.command.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.command.Command;
import org.springframework.stereotype.Component;

@Component
public class StartCommand implements Command {

    @Override
    public String getCommandName() {
        return "/start";
    }

    @Override
    public String getDescription() {
        return "start working with a bot";
    }

    @Override
    public SendMessage handle(Update update) {
        return new SendMessage(
            update.message().chat().id(),
            "Welcome to the link tracking bot! For more information, type /help."
        );
    }
}
