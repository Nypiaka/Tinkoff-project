package edu.java.bot.service.command.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.command.Command;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class HelpCommand implements Command {

    private final List<Command> commands;

    private String description = null;

    public HelpCommand(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public String getCommandName() {
        return "/help";
    }

    @Override
    public String getDescription() {
        return "/help - get help on working with a bot";
    }

    public String fullDescription() {
        if (description == null) {
            var builder = new StringBuilder("List of available commands:");
            builder.append(System.lineSeparator()).append(System.lineSeparator());
            commands.forEach(command ->
                builder.append(command.getDescription())
                    .append(System.lineSeparator()).append(System.lineSeparator()));
            description = builder.toString();
            return description;
        }
        return description;
    }

    @Override
    public SendMessage handle(Update update, String[] parts) {
        return new SendMessage(update.message().chat().id(), fullDescription());
    }
}
