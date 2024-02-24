package edu.java.bot.service.processor;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.command.Command;
import edu.java.bot.service.command.commands.HelpCommand;
import edu.java.bot.service.command.commands.ListCommand;
import edu.java.bot.service.command.commands.StartCommand;
import edu.java.bot.service.command.commands.TrackCommand;
import edu.java.bot.service.command.commands.UntrackCommand;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component

public class UserMessageProcessor {

    private final Map<String, Command> commands;

    @Autowired
    public UserMessageProcessor(
        HelpCommand helpCommand,
        ListCommand listCommand,
        StartCommand startCommand,
        TrackCommand trackCommand,
        UntrackCommand untrackCommand
    ) {
        commands = new HashMap<>();
        commands.put(helpCommand.getCommandName(), helpCommand);
        commands.put(listCommand.getCommandName(), listCommand);
        commands.put(startCommand.getCommandName(), startCommand);
        commands.put(trackCommand.getCommandName(), trackCommand);
        commands.put(untrackCommand.getCommandName(), untrackCommand);
    }

    public SendMessage process(Update update) {
        if (update.message() != null) {
            if (update.message().text() == null) {
                return unknownCommand(update);
            }
            var messages = update.message().text().split(" ");
            var message = messages[0];
            if (commands.containsKey(message)) {
                var command = commands.get(message);
                return command.handle(update, Arrays.stream(messages).toList());
            }
            return unknownCommand(update);
        }
        return null;
    }

    private SendMessage unknownCommand(Update update) {
        return new SendMessage(update.message().chat().id(), "Unknown command");
    }
}
