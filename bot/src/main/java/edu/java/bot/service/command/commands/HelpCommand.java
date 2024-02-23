package edu.java.bot.service.command.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.command.Command;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand implements Command {

    @Autowired
    private final List<Command> commands;

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

    @Override
    public SendMessage handle(Update update, List<String> parts) {
        var builder = new StringBuilder("List of available commands:");
        builder.append(System.lineSeparator()).append(System.lineSeparator());
        commands.forEach(command ->
            builder.append(command.getDescription())
                .append(System.lineSeparator()).append(System.lineSeparator()));
        return new SendMessage(update.message().chat().id(), builder.toString());
    }
}
