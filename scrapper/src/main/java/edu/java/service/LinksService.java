package edu.java.service;

import edu.java.utils.dto.ListLinksResponse;
import java.util.Collection;
import java.util.List;

public interface LinksService {
    List<String> getList(Long id);

    boolean saveLinkAndUpdate(Long id, String link, String content);

    boolean removeLink(Long id, String link);

    boolean containsLink(Long id, String link);

    boolean updateLink(List<Long> id, String link, String content);

    boolean registerChat(Long id);

    boolean removeChat(Long id);

    boolean containsChat(Long id);

    Collection<String> getAllLinks();

    Collection<String> getAllLinks(String sqlPart);

    List<Long> getChatsByLink(String link);

    long getId(String link);

    String getLastUpdate(String link);

    void saveLinkAndUpdate(String link, String update);

    List<Long> getLinksIdsByChatId(Long chatId);

    ListLinksResponse getAllByChatId(Long chatId);
}
