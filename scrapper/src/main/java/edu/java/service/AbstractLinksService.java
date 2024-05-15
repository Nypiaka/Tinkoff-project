package edu.java.service;

import edu.java.dao.LinksDao;
import edu.java.utils.dto.ListLinksResponse;
import java.util.Date;
import java.util.List;

public abstract class AbstractLinksService implements LinksService {

    protected abstract LinksDao getDao();

    @Override
    public boolean saveLinkInChat(Long chatId, String link) {
        if (!getDao().exists(link)) {
            getDao().saveLink(link);
            var linkId = getDao().getId(link);
            getDao().updateContent(linkId, "", new Date());
        }
        var alreadyExists = getDao().chatContainsLink(chatId, link);
        if (alreadyExists) {
            return false;
        }
        getDao().saveLinkToChat(chatId, link);
        return true;
    }

    @Override
    public boolean removeLinkFromChat(Long chatId, String link) {
        var foundLink = getDao().exists(link);
        if (!foundLink) {
            return false;
        }
        var chatContainsLink = getDao().chatContainsLink(chatId, link);
        if (!chatContainsLink) {
            return false;
        }
        return getDao().removeLinkFromChat(chatId, link);
    }

    @Override
    public boolean removeChat(Long chatId) {
        if (!getDao().containsChat(chatId)) {
            return false;
        }
        return getDao().removeChat(chatId);
    }

    @Override
    public boolean registerChat(Long chatId) {
        return true;
    }

    @Override
    public ListLinksResponse getAllByChatId(Long id) {
        return getDao().getAllByChatId(id);
    }

    @Override
    public boolean containsChatAndLink(Long chatId, String link) {
        return getDao().chatContainsLink(chatId, link);
    }

    @Override
    public boolean containsChat(Long id) {
        return getDao().containsChat(id);
    }

    @Override
    public Iterable<String> getAllLinksForInterval(Integer updateTime) {
        return getDao().getAllLinksForInterval(updateTime);
    }

    @Override
    public long getId(String link) {
        return getDao().getId(link);
    }

    @Override
    public String getLastUpdate(String link) {
        return getDao().getLastUpdate(link);
    }

    @Override
    public List<Long> getChatsByLink(String link) {
        return getDao().getChatsByLink(link);
    }

    @Override
    public void update(String s, String update) {
        getDao().updateContent(getDao().getId(s), update, new Date());
    }
}
