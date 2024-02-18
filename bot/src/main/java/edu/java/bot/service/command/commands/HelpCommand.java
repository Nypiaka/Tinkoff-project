package edu.java.bot.service.command.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.command.Command;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HelpCommand implements Command {

    private final List<Command> commands;

    @Override
    public String getCommandName() {
        return "/help";
    }

    @Override
    public String getDescription() {
        return "get help on working with a bot";
    }

    @Override
    public SendMessage handle(Update update) {
        var builder = new StringBuilder("List of available commands:");
        builder.append(System.lineSeparator()).append(System.lineSeparator());
        commands.forEach(command ->
            builder.append(command.getCommandName()).append(" - ").append(command.getDescription())
                .append(System.lineSeparator()).append(System.lineSeparator()));
        return new SendMessage(update.message().chat().id(), builder.toString());
    }
}
