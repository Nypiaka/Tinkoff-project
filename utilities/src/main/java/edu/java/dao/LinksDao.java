package edu.java.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class LinksDao {

    private static final String USER = "postgres";
    private static final String PASSWORD = USER;

    private static final String ON_CONFLICT_DO_NOTHING = "') on conflict do nothing";

    private static final String ON_CONFLICT_DO_NOTHING_2 = ") on conflict do nothing";
    private final Connection connection;

    public LinksDao(String url) throws SQLException {
        this.connection = DriverManager.getConnection(url, USER, PASSWORD);
    }

    public List<String> getList(Long id) {
        try {
            var result = connection.createStatement()
                .executeQuery("select l.link from chats c join links l on l.id = c.link_id where c.chat_id = " + id);
            var links = new ArrayList<String>();
            while (result.next()) {
                links.add(result.getString(1));
            }
            return links;
        } catch (SQLException e) {
            return List.of();
        }
    }

    private long getId(String link) throws SQLException {
        var result =
            connection.createStatement().executeQuery("select l.id from links l where l.link = '" + link + "'");
        if (result.next()) {
            return result.getLong(1);
        }
        throw new IllegalStateException();
    }

    public boolean saveLink(Long id, String link, String content) {
        try {
            save(link, content);
            var linkId = getId(link);
            var exists = connection.createStatement()
                .executeQuery(
                    "select count(*) from chats c where c.chat_id = " + id + " and c.link_id = " + linkId);
            if (exists.next() && exists.getLong(1) == 0) {
                connection.createStatement()
                    .executeUpdate("insert into chats (chat_id, link_id) values (" + id + ", " + linkId + ")");
            }
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean removeLink(Long id, String link) {
        try {
            var prepared = connection.prepareStatement(
                """
                    delete from chats
                    using chats c
                    join links t on c.link_id = t.id
                    where c.chat_id = ? and t.link = ?;
                    """);
            prepared.setLong(1, id);
            prepared.setString(2, link);
            prepared.execute();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean containsLink(Long id, String link) {
        try {
            var prepared = connection.prepareStatement(
                "select count(*) from chats c JOIN links l on c.link_id = l.id where c.chat_id = ? AND l.link = ?");
            prepared.setLong(1, id);
            prepared.setString(2, link);
            var result = prepared.executeQuery();
            result.next();
            int count = result.getInt(1);

            return count > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateLink(List<Long> id, String link, String content) {
        var curLink = link.toLowerCase();
        var success = true;
        for (var i : id) {
            success &= saveLink(i, curLink, content);
        }
        return success;
    }

    public boolean registerChat(Long id) {
        try {
            connection.createStatement()
                .executeUpdate("insert into chats (chat_id) values (" + id + ON_CONFLICT_DO_NOTHING_2);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean removeChat(Long id) {
        try {
            connection.createStatement()
                .executeUpdate("delete from chats where chat_id = " + id);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean containsChat(Long id) {
        try {
            var result = connection.createStatement()
                .executeQuery("select count(*) from chats where chat_id = " + id);
            result.next();
            int count = result.getInt(1);
            return count > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public Collection<String> getAllLinks() {
        try {
            var result = connection.createStatement().executeQuery("select link from links");
            var links = new HashSet<String>();
            while (result.next()) {
                links.add(result.getString(1));
            }

            return links;
        } catch (SQLException e) {
            return new HashSet<>();
        }
    }

    public String getLastUpdate(String link) {
        try {
            var prepared = connection.prepareStatement(
                "select c.content from content c join links l on l.id = c.link_id where l.link = ?");
            prepared.setString(1, link);
            var result = prepared.executeQuery();
            String content = null;
            if (result.next()) {
                content = result.getString(1);
            }

            return content;
        } catch (SQLException e) {
            return null;
        }
    }

    public boolean save(String link, String update) {
        try {
            connection.createStatement()
                .executeUpdate("insert into links (link) values ('" + link + ON_CONFLICT_DO_NOTHING);
            var linkId = getId(link);
            var exists = connection.createStatement()
                .executeQuery("select count(*) from content c where c.link_id = " + linkId);
            if (!exists.next()) {
                return false;
            }
            if (exists.getLong(1) == 0) {
                connection.createStatement()
                    .executeUpdate(
                        "insert into content (link_id, content) values (" + linkId + ", '" + update + "')");
                return true;
            } else {
                connection.createStatement()
                    .executeUpdate("update content set content = '" + update + "' where link_id = " + linkId);
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
    }
}
