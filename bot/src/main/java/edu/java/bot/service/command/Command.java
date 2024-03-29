package edu.java.bot.service.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;

public interface Command {
    String getCommandName();

    String getDescription();

    default boolean isApplicable(String command) {
        return getCommandName().equals(command);
    }

    SendMessage handle(Update update, List<String> parts);

}
