package edu.java.bot.service.command.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.service.command.Command;
import edu.java.dao.LinksDao;
import edu.java.utils.Utils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UntrackCommand implements Command {

    @Autowired
    private LinksDao linksDao;

    @Override
    public String getCommandName() {
        return "/untrack";
    }

    @Override
    public String getDescription() {
        return "/untrack {link} - stop tracking link";
    }

    @Override
    public SendMessage handle(Update update, List<String> parts) {
        var content = update.message().text().split(" ");
        String ans;
        if (content.length < 2) {
            ans = "Please, insert link to remove in format \"/untrack {link}\".";
        } else {
            if (Utils.validateLink(parts.get(1))) {
                var saved = linksDao.removeLink(update.message().chat().id(), update.message().text().split(" ")[1]);
                ans = saved ? "Link removed successful." : "Oops! Link was not removed.";
            } else {
                ans = "Wrong link format!";
            }
        }
        return new SendMessage(update.message().chat().id(), ans);
    }
}
