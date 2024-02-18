package edu.java.bot.service.command.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.dao.Dao;
import edu.java.bot.service.command.Command;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListCommand implements Command {
    @Autowired
    private Dao dao;

    @Override
    public String getCommandName() {
        return "/list";
    }

    @Override
    public String getDescription() {
        return "get a list of all available links";
    }

    @Override
    public SendMessage handle(Update update) {
        var links = dao.getList(update.message().chat().id());
        return new SendMessage(update.message().chat().id(), "Tracked links:\n" + Strings.join(links, '\n'));
    }
}
