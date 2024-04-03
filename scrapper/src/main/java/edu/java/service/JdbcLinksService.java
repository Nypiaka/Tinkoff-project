package edu.java.service;

import edu.java.dao.JdbcLinksDao;
import edu.java.utils.dto.ListLinksResponse;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JdbcLinksService implements LinksService {

    private final JdbcLinksDao jdbcLinksDao;

    @Override
    public boolean saveLinkInChat(Long chatId, String link) {
        if (!jdbcLinksDao.exists(link)) {
            jdbcLinksDao.saveLink(link);
            var linkId = jdbcLinksDao.getId(link);
            jdbcLinksDao.updateContent(linkId, "", new Date());
        }
        var alreadyExists = jdbcLinksDao.chatContainsLink(chatId, link);
        if (alreadyExists) {
            return false;
        }
        jdbcLinksDao.saveLinkToChat(chatId, link);
        return true;
    }

    @Override
    public boolean removeLinkFromChat(Long chatId, String link) {
        var foundLink = jdbcLinksDao.exists(link);
        if (!foundLink) {
            return false;
        }
        var chatContainsLink = jdbcLinksDao.chatContainsLink(chatId, link);
        if (!chatContainsLink) {
            return false;
        }
        return jdbcLinksDao.removeLinkFromChat(chatId, link);
    }

    @Override
    public boolean removeChat(Long chatId) {
        if (!jdbcLinksDao.containsChat(chatId)) {
            return false;
        }
        return jdbcLinksDao.removeChat(chatId);
    }

    @Override
    public boolean registerChat(Long chatId) {
        return true;
    }

    @Override
    public ListLinksResponse getAllByChatId(Long id) {
        return jdbcLinksDao.getAllByChatId(id);
    }

    @Override
    public boolean containsChatAndLink(Long chatId, String link) {
        return jdbcLinksDao.chatContainsLink(chatId, link);
    }

    @Override
    public boolean containsChat(Long id) {
        return jdbcLinksDao.containsChat(id);
    }

    @Override
    public Iterable<String> getAllLinksForInterval(Integer updateTime) {
        return jdbcLinksDao.getAllLinksForInterval(updateTime);
    }

    @Override
    public long getId(String link) {
        return jdbcLinksDao.getId(link);
    }

    @Override
    public String getLastUpdate(String link) {
        return jdbcLinksDao.getLastUpdate(link);
    }

    @Override
    public List<Long> getChatsByLink(String link) {
        return jdbcLinksDao.getChatsByLink(link);
    }

    @Override
    public void update(String s, String update) {
        jdbcLinksDao.updateContent(jdbcLinksDao.getId(s), update, new Date());
    }
}
