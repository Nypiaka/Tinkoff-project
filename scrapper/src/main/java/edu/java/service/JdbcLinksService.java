package edu.java.service;

import edu.java.dao.JdbcLinksDao;
import edu.java.scheduler.LinkUpdaterScheduler;
import edu.java.utils.dto.LinkResponse;
import edu.java.utils.dto.ListLinksResponse;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
public class JdbcLinksService implements LinksService {
    @Autowired
    private JdbcLinksDao jdbcLinksDao;

    @Autowired
    @Lazy
    private LinkUpdaterScheduler linkUpdaterScheduler;

    @Override
    public List<String> getList(Long id) {
        return jdbcLinksDao.getList(id);
    }

    @Override
    public long getId(String link) {
        return jdbcLinksDao.getId(link);
    }

    @Override
    public boolean saveLinkAndUpdate(Long id, String link, String content) {
        if (!jdbcLinksDao.exists(link)) {
            jdbcLinksDao.saveLink(link);
        }
        try {
            var res = jdbcLinksDao.saveLinkToChat(id, link, content);
            linkUpdaterScheduler.forceUpdate(link);
            return res;
        } catch (DuplicateKeyException e) {
            return true;
        }
    }

    @Override
    public boolean removeLink(Long id, String link) {
        return jdbcLinksDao.removeLink(id, link);
    }

    @Override
    public boolean containsLink(Long id, String link) {
        return jdbcLinksDao.containsLink(id, link);
    }

    @Override
    public boolean updateLink(List<Long> id, String link, String content) {
        var curLink = link.toLowerCase();
        var success = true;
        for (var i : id) {
            success &= saveLinkAndUpdate(i, curLink, content);
        }
        return success;
    }

    @Override
    public boolean registerChat(Long id) {
        return jdbcLinksDao.registerChat(id);
    }

    @Override
    public boolean removeChat(Long id) {
        return jdbcLinksDao.removeChat(id);
    }

    @Override
    public boolean containsChat(Long id) {
        return jdbcLinksDao.containsChat(id);
    }

    @Override
    public Collection<String> getAllLinks() {
        return jdbcLinksDao.getAllLinks();
    }

    @Override
    public Collection<String> getAllLinks(String sqlPart) {
        return jdbcLinksDao.getAllLinks(sqlPart);
    }

    @Override
    public List<Long> getChatsByLink(String link) {
        return jdbcLinksDao.getChatsByLink(link);
    }

    @Override
    public String getLastUpdate(String link) {
        return jdbcLinksDao.getLastUpdate(link);
    }

    @Override
    public void saveLinkAndUpdate(String link, String update) {
        var linkToWork = link.toLowerCase();
        try {
            if (!jdbcLinksDao.exists(link)) {
                jdbcLinksDao.saveLink(link);
            }
        } catch (DuplicateKeyException ignored) {
        } finally {
            var linkId = getId(linkToWork);
            var now = new Date();
            jdbcLinksDao.updateContent(linkId, update, now);
        }
        jdbcLinksDao.exists(link);
    }

    @Override
    public List<Long> getLinksIdsByChatId(Long chatId) {
        return jdbcLinksDao.getLinksIdsByChatId(chatId);
    }

    @Override
    public ListLinksResponse getAllByChatId(Long chatId) {
        var links = jdbcLinksDao.getList(chatId);
        var ids = jdbcLinksDao.getLinksIdsByChatId(chatId);
        return new ListLinksResponse(IntStream.range(0, links.size()).mapToObj(
            ind -> new LinkResponse(ids.get(ind), URI.create(links.get(ind)))
        ).toList(), links.size());
    }

    @Override
    public List<String> getAllLinksForInterval(int mins) {
        return jdbcLinksDao.getAllLinksForInterval(mins);
    }
}
