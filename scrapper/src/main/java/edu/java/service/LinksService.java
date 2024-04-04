package edu.java.service;

import edu.java.utils.dto.ListLinksResponse;
import java.util.List;

public interface LinksService {
    boolean saveLinkInChat(Long chatId, String link);

    boolean removeLinkFromChat(Long chatId, String link);

    boolean removeChat(Long chatId);

    boolean registerChat(Long chatId);

    ListLinksResponse getAllByChatId(Long id);

    boolean containsChatAndLink(Long chatId, String link);

    boolean containsChat(Long id);

    Iterable<String> getAllLinksForInterval(Integer updateTime);

    long getId(String link);

    String getLastUpdate(String link);

    List<Long> getChatsByLink(String link);

    void update(String s, String update);
}
