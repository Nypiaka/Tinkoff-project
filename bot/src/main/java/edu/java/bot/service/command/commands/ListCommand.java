package edu.java.bot.service.command.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.command.Command;
import edu.java.dao.LinksDao;
import java.util.List;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ListCommand implements Command {
    @Autowired
    private LinksDao linksDao;

    @Override
    public String getCommandName() {
        return "/list";
    }

    @Override
    public String getDescription() {
        return "/list - get a list of all available links";
    }

    @Override
    public SendMessage handle(Update update, List<String> parts) {
        var links = linksDao.getList(update.message().chat().id());
        return new SendMessage(update.message().chat().id(), "Tracked links:\n" + Strings.join(links, '\n'));
    }
}
