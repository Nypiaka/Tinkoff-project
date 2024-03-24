package edu.java.bot.service;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.bot.LinksRefreshCheckerBot;
import edu.java.utils.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatsService {
    private final LinksRefreshCheckerBot linksRefreshCheckerBot;

    public boolean updateChatsInfo(LinkUpdate req) {
        try {
            for (var id : req.getTgChatIds()) {
                linksRefreshCheckerBot.execute(new SendMessage(
                    id, "Updates by link: " + req.getUrl() + "\n" + req.getDescription()
                ));
            }
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
