package edu.java.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class JdbcLinksDao implements LinksDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcLinksDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional
    public List<String> getList(Long id) {
        return jdbcTemplate.queryForList(
            "select l.link from chats_to_links c join links l on l.id = c.link_id where c.chat_id = ?",
            String.class,
            id
        );
    }

    @Transactional
    public long getId(String link) {
        var result = jdbcTemplate.queryForList("select l.id from links l where l.link = ?", Long.class, link);
        return result.getFirst();
    }

    @Transactional
    public boolean saveLinkToChat(Long id, String link, String content) {
        return jdbcTemplate.update(
            "insert into chats_to_links (chat_id, link_id) values (?, ?)",
            id,
            getId(link.toLowerCase())
        )
            == 1;
    }

    @Transactional
    public boolean removeLink(Long id, String link) {
        return jdbcTemplate.update(
            """
                delete from chats_to_links
                using chats_to_links c
                join links t on c.link_id = t.id
                where c.chat_id = ? and t.link = ?;
                """, id, link.toLowerCase()) > 0;
    }

    @Transactional
    public boolean containsLink(Long id, String link) {
        var count = jdbcTemplate.queryForObject(
            "select count(*) from chats_to_links c join links l on c.link_id = l.id where c.chat_id = ? and l.link = ?",
            Long.class,
            id,
            link.toLowerCase()
        );
        return count != null && count != 0;
    }

    @Transactional
    public boolean registerChat(Long id) {
        return true;
    }

    @Transactional
    public boolean removeChat(Long id) {
        jdbcTemplate.update("delete from chats_to_links where chat_id = ?", id);
        return !containsChat(id);
    }

    @Transactional
    public boolean containsChat(Long id) {
        var count =
            jdbcTemplate.queryForObject("select count(*) from chats_to_links where chat_id = ?", Long.class, id);
        return count != null && count > 0;
    }

    @Transactional
    public Collection<String> getAllLinks() {
        return jdbcTemplate.queryForList("select link from links", String.class);
    }

    @Transactional
    public Collection<String> getAllLinks(String sqlPart) {
        return jdbcTemplate.queryForList(
            "select l.link from links l join content_by_link c on c.link_id = l.id " + sqlPart,
            String.class
        );
    }

    @Transactional
    public List<Long> getChatsByLink(String link) {
        return jdbcTemplate.queryForList(
            "select c.chat_id from chats_to_links c join links l on l.id = c.link_id where l.link = ?",
            Long.class,
            link
        );
    }

    @Transactional
    public String getLastUpdate(String link) {
        return jdbcTemplate.queryForList(
            "select c.content from content_by_link c join links l on l.id = c.link_id where l.link = ?",
            String.class,
            link
        ).getFirst();
    }

    @Transactional
    public boolean exists(String link) {
        var count = jdbcTemplate.queryForObject("select count(*) from links l where l.link = ?", Long.class, link);
        return count != null && count != 0;
    }

    @Transactional
    public boolean saveLink(String link) {
        jdbcTemplate.update("insert into links (link) values (?)", link);
        return true;
    }

    @Transactional
    public boolean updateContent(Long linkId, String update, Date date) {
        jdbcTemplate.update(
            "insert into content_by_link (link_id, content, updated_at) values (?, ?, ?)"
                + " on conflict (link_id) do update set content = ?, updated_at = ?",
            linkId,
            update,
            date,
            update,
            date
        );
        return true;
    }

    @Transactional
    public List<Long> getLinksIdsByChatId(Long chatId) {
        return jdbcTemplate.queryForList(
            "select c.link_id from chats_to_links c where c.chat_id = ?",
            Long.class,
            chatId
        );
    }
}

