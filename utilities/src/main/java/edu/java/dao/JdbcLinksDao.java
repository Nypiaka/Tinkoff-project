package edu.java.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.dao.DuplicateKeyException;
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
            "select l.link from chats c join links l on l.id = c.link_id where c.chat_id = ?",
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
    public boolean saveLink(Long id, String link, String content) {
        save(link, content);
        try {
            return jdbcTemplate.update(
                "insert into chats (chat_id, link_id) values (?, ?)",
                id,
                getId(link.toLowerCase())
            )
                == 1;
        } catch (DuplicateKeyException e) {
            return true;
        }
    }

    @Transactional
    public boolean removeLink(Long id, String link) {
        return jdbcTemplate.update(
            """
                delete from chats
                using chats c
                join links t on c.link_id = t.id
                where c.chat_id = ? and t.link = ?;
                """, id, link.toLowerCase()) > 0;
    }

    @Transactional
    public boolean containsLink(Long id, String link) {
        var count = jdbcTemplate.queryForObject(
            "select count(*) from chats c join links l on c.link_id = l.id where c.chat_id = ? and l.link = ?",
            Long.class,
            id,
            link.toLowerCase()
        );
        if (count == null) {
            return false;
        }
        return count != 0;
    }

    @Transactional
    public boolean updateLink(List<Long> id, String link, String content) {
        var curLink = link.toLowerCase();
        var success = true;
        for (var i : id) {
            success &= saveLink(i, curLink, content);
        }
        return success;
    }

    @Transactional
    public boolean registerChat(Long id) {
        return jdbcTemplate.update("insert into chats (chat_id) values (?)", id) > 0;
    }

    @Transactional
    public boolean removeChat(Long id) {
        return jdbcTemplate.update("delete from chats where chat_id = ?", id) != 0;
    }

    @Transactional
    public boolean containsChat(Long id) {
        var count = jdbcTemplate.queryForObject("select count(*) from chats where chat_id = ?", Long.class, id);
        if (count == null) {
            return false;
        }
        return count > 0;
    }

    @Transactional
    public Collection<String> getAllLinks() {
        return jdbcTemplate.queryForList("select link from links", String.class);
    }

    @Transactional
    public Collection<String> getAllLinks(String sqlPart) {
        return jdbcTemplate.queryForList(
            "select l.link from links l join content c on c.link_id = l.id " + sqlPart,
            String.class
        );
    }

    @Transactional
    public List<Long> getChatsByLink(String link) {
        return jdbcTemplate.queryForList(
            "select c.chat_id from chats c join links l on l.id = c.link_id where l.link = ?",
            Long.class,
            link
        );
    }

    @Transactional
    public String getLastUpdate(String link) {
        return jdbcTemplate.queryForList(
            "select c.content from content c join links l on l.id = c.link_id where l.link = ?",
            String.class,
            link
        ).getFirst();
    }

    @Transactional
    private boolean exists(String link) {
        var count = jdbcTemplate.queryForObject("select count(*) from links l where l.link = ?", Long.class, link);
        return count != null && count != 0;
    }

    @Transactional
    public boolean save(String link, String update) {
        var linkToWork = link.toLowerCase();
        try {
            if (!exists(link)) {
                jdbcTemplate.update("insert into links (link) values (?)", linkToWork);
            }
        } catch (DuplicateKeyException ignored) {
        } finally {
            var linkId = getId(linkToWork);
            var now = new Date();
            jdbcTemplate.update(
                "insert into content (link_id, content, updated_at) values (?, ?, ?)"
                    + " on conflict (link_id) do update set content = ?, updated_at = ?",
                linkId,
                update,
                now,
                update,
                now
            );
        }
        return exists(link);
    }
}

