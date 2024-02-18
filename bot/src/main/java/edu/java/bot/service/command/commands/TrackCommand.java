package edu.java.bot.service.command.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.dao.Dao;
import edu.java.bot.service.command.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TrackCommand implements Command {
    @Autowired
    private Dao dao;

    @Override
    public String getCommandName() {
        return "/track {link}";
    }

    @Override
    public String getDescription() {
        return "start tracking link";
    }

    @Override
    public SendMessage handle(Update update) {
        var content = update.message().text().split(" ");
        String ans;
        if (content.length < 2) {
            ans = "Please, insert link to track in format \"/track {link}\".";
        } else {
            var saved = dao.saveLink(update.message().chat().id(), update.message().text().split(" ")[1]);
            ans = saved ? "Link saved successful." : "Oops! Link was not saved.";
        }
        return new SendMessage(update.message().chat().id(), ans);
    }
}
