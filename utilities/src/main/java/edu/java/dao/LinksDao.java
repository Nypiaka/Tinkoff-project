package edu.java.dao;

import java.util.Collection;
import java.util.List;

public interface LinksDao {
    List<String> getList(Long id);

    boolean saveLinkToChat(Long id, String link, String content);

    boolean removeLink(Long id, String link);

    boolean containsLink(Long id, String link);

    boolean registerChat(Long id);

    boolean removeChat(Long id);

    boolean containsChat(Long id);

    Collection<String> getAllLinks();

    Collection<String> getAllLinks(String sqlPart);

    List<Long> getChatsByLink(String link);

    long getId(String link);

    String getLastUpdate(String link);

    List<Long> getLinksIdsByChatId(Long chatId);

    List<String> getAllLinksForInterval(int mins);
}

