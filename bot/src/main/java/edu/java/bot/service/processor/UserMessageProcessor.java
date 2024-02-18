package edu.java.bot.service.processor;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.command.Command;
import edu.java.bot.service.command.Commands;
import edu.java.bot.service.command.commands.HelpCommand;
import edu.java.bot.service.command.commands.ListCommand;
import edu.java.bot.service.command.commands.StartCommand;
import edu.java.bot.service.command.commands.TrackCommand;
import edu.java.bot.service.command.commands.UntrackCommand;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component

public class UserMessageProcessor {

    private final Map<Commands, Command> commands;

    private static final Map<String, Commands> COMMAND_BY_MESSAGE = Map.of(
        "/start", Commands.START,
        "/track", Commands.TRACK,
        "/untrack", Commands.UNTRACK,
        "/help", Commands.HELP,
        "/list", Commands.LIST
    );

    @Autowired
    public UserMessageProcessor(
        HelpCommand helpCommand,
        ListCommand listCommand,
        StartCommand startCommand,
        TrackCommand trackCommand,
        UntrackCommand untrackCommand
    ) {
        commands = new HashMap<>();
        commands.put(Commands.HELP, helpCommand);
        commands.put(Commands.LIST, listCommand);
        commands.put(Commands.START, startCommand);
        commands.put(Commands.TRACK, trackCommand);
        commands.put(Commands.UNTRACK, untrackCommand);
    }

    public SendMessage process(Update update) {
        if (update.message() != null) {
            var message = update.message().text().split(" ")[0];
            if (commands.containsKey(COMMAND_BY_MESSAGE.get(message))) {
                var command = commands.get(COMMAND_BY_MESSAGE.get(message));
                return command.handle(update);
            }
            return new SendMessage(update.message().chat().id(), "Unknown command");
        }
        return null;
    }
}
