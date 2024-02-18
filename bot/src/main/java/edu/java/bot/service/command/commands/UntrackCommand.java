package edu.java.bot.service.command.commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.dao.Dao;
import edu.java.bot.service.command.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UntrackCommand implements Command {

    @Autowired
    private Dao dao;

    @Override
    public String getCommandName() {
        return "/untrack {link}";
    }

    @Override
    public String getDescription() {
        return "stop tracking link";
    }

    @Override
    public SendMessage handle(Update update) {
        var content = update.message().text().split(" ");
        String ans;
        if (content.length < 2) {
            ans = "Please, insert link to remove in format \"/track {link}\".";
        } else {
            var saved = dao.removeLink(update.message().chat().id(), update.message().text().split(" ")[1]);
            ans = saved ? "Link removed successful." : "Oops! Link was not removed.";
        }
        return new SendMessage(update.message().chat().id(), ans);
    }
}
