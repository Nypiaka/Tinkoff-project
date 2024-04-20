package edu.java.dao;

import edu.java.utils.dto.ListLinksResponse;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface LinksDao {
    List<String> getList(Long id);

    boolean saveLinkToChat(Long id, String link);

    boolean removeLinkFromChat(Long id, String link);

    boolean chatContainsLink(Long id, String link);

    boolean registerChat(Long id);

    boolean removeChat(Long id);

    boolean containsChat(Long id);

    List<Long> getChatsByLink(String link);

    long getId(String link);

    String getLastUpdate(String link);

    List<String> getAllLinksForInterval(int mins);

    boolean exists(String link);

    boolean saveLink(String link);

    boolean updateContent(Long linkId, String s, Date date);

    ListLinksResponse getAllByChatId(Long id);

    Collection<String> getAllLinks();
}

