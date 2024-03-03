package edu.java.bot.service.command.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.BotUtils;
import edu.java.bot.dao.LinksDao;
import edu.java.bot.service.command.Command;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrackCommand implements Command {
    @Autowired
    private LinksDao linksDao;

    @Override
    public String getCommandName() {
        return "/track";
    }

    @Override
    public String getDescription() {
        return "/track {link} - start tracking link";
    }

    @Override
    public SendMessage handle(Update update, List<String> parts) {
        String ans;
        if (parts.size() < 2) {
            ans = "Please, insert link to track in format \"/track {link}\".";
        } else {
            if (BotUtils.validateLink(parts.get(1))) {
                var saved =
                    linksDao.saveLink(update.message().chat().id(), update.message().text().split(" ")[1], "");
                ans = saved ? "Link saved successful." : "Oops! Link was not saved.";
            } else {
                ans = "Wrong link format!";
            }
        }
        return new SendMessage(update.message().chat().id(), ans);
    }
}
