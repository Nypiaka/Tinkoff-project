package edu.java.bot.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.response.BaseResponse;
import edu.java.bot.Bot;
import edu.java.bot.service.processor.UserMessageProcessor;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LinksRefreshCheckerBot implements Bot {

    private final TelegramBot bot;

    @Autowired
    private UserMessageProcessor userMessageProcessor;

    public LinksRefreshCheckerBot(String token) {
        this.bot = new TelegramBot(token);
        bot.setUpdatesListener(this);
    }

    @Override public <T extends BaseRequest<T, R>, R extends BaseResponse> void execute(BaseRequest<T, R> request) {
        bot.execute(request);
    }

    @Override public int process(List<Update> updates) {
        updates.forEach(update -> {
            var res = userMessageProcessor.process(update);
            if (res != null) {
                execute(res);
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
