package edu.java.bot.service;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.bot.LinksRefreshCheckerBot;
import edu.java.utils.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

@RequiredArgsConstructor
@Service
public class ChatsService {
    private final LinksRefreshCheckerBot linksRefreshCheckerBot;

    public boolean updateChatsInfo(LinkUpdate req) {
        RuntimeException ex = null;
        for (var id : req.getTgChatIds()) {
            try {
                linksRefreshCheckerBot.execute(new SendMessage(
                    id, "Updates by link: " + req.getUrl() + "\n" + req.getDescription()
                ));
            } catch (RuntimeException e) {
                ex = e;
            }
        }
        if (ex != null) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
        return true;
    }
}
