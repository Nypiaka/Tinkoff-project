package edu.java.dao;

import edu.java.jooq.publics.tables.ChatsToLinks;
import edu.java.utils.dto.LinkResponse;
import edu.java.utils.dto.ListLinksResponse;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;
import static edu.java.jooq.publics.Tables.CHATS_TO_LINKS;
import static edu.java.jooq.publics.Tables.CONTENT_BY_LINK;
import static edu.java.jooq.publics.Tables.LINKS;

@Component
public class JooqLinksDao implements LinksDao {

    private final DSLContext context;

    public JooqLinksDao(DataSource dataSource) throws SQLException {
        this.context = DSL.using(dataSource.getConnection());
    }

    @Transactional
    public List<String> getList(Long id) {
        return context.select(LINKS.fields())
            .from(CHATS_TO_LINKS).join(LINKS)
            .on(LINKS.ID.eq(CHATS_TO_LINKS.LINK_ID))
            .where(CHATS_TO_LINKS.CHAT_ID.eq(id)).fetch(LINKS.LINK);
    }

    @Transactional
    public boolean saveLinkToChat(Long id, String link) {
        return context.insertInto(
            CHATS_TO_LINKS,
            CHATS_TO_LINKS.CHAT_ID,
            ChatsToLinks.CHATS_TO_LINKS.LINK_ID
        ).values(id, getId(link.toLowerCase())).execute() == 1;
    }

    @Transactional
    public boolean removeLinkFromChat(Long id, String link) {
        return context.deleteFrom(CHATS_TO_LINKS)
            .where(CHATS_TO_LINKS.LINK_ID
                .eq(
                    context.select(LINKS.ID)
                        .from(LINKS)
                        .where(LINKS.LINK.eq(link.toLowerCase()))
                )
            )
            .and(CHATS_TO_LINKS.CHAT_ID.eq(id))
            .execute() > 0;
    }

    @Transactional
    public boolean chatContainsLink(Long id, String link) {
        var res = context.selectCount()
            .from(CHATS_TO_LINKS)
            .join(LINKS)
            .on(CHATS_TO_LINKS.LINK_ID.eq(LINKS.ID))
            .where(CHATS_TO_LINKS.CHAT_ID.eq(id).and(LINKS.LINK.eq(link.toLowerCase()))).fetch().getFirst().value1();
        return res > 0;
    }

    @Transactional
    public boolean registerChat(Long id) {
        return true;
    }

    @Transactional
    public boolean removeChat(Long id) {
        context.deleteFrom(CHATS_TO_LINKS).where(CHATS_TO_LINKS.CHAT_ID.eq(id)).execute();
        return !containsChat(id);
    }

    @Transactional
    public boolean containsChat(Long id) {
        var count = context.selectCount()
            .from(CHATS_TO_LINKS)
            .where(CHATS_TO_LINKS.CHAT_ID.eq(id))
            .fetchOne(0, Long.class);
        return count != null && count > 0;
    }

    @Transactional
    public List<Long> getChatsByLink(String link) {
        return context.select(CHATS_TO_LINKS.CHAT_ID).from(CHATS_TO_LINKS).join(LINKS)
            .on(LINKS.ID.eq(CHATS_TO_LINKS.LINK_ID)).where(LINKS.LINK.eq(link)).fetch(CHATS_TO_LINKS.CHAT_ID);
    }

    @Transactional
    public long getId(String link) {
        return context.select(LINKS.ID).from(LINKS).where(LINKS.LINK.eq(link.toLowerCase())).fetch(LINKS.ID).getFirst();
    }

    @Transactional
    public String getLastUpdate(String link) {
        var result = context.select(CONTENT_BY_LINK.CONTENT)
            .from(CONTENT_BY_LINK)
            .join(LINKS).on(CONTENT_BY_LINK.LINK_ID.eq(LINKS.ID))
            .where(LINKS.LINK.eq(link))
            .fetchOne();
        if (result != null) {
            return result.get(CONTENT_BY_LINK.CONTENT);
        } else {
            return null;
        }
    }

    @Transactional
    public List<String> getAllLinksForInterval(int mins) {
        return context.select(LINKS.LINK)
            .from(LINKS)
            .join(CONTENT_BY_LINK).on(CONTENT_BY_LINK.LINK_ID.eq(LINKS.ID))
            .where(CONTENT_BY_LINK.UPDATED_AT.le(LocalDateTime.now().minusMinutes(mins)))
            .fetch(LINKS.LINK);
    }

    @Transactional
    public boolean exists(String link) {
        var count = context.selectCount()
            .from(LINKS)
            .where(LINKS.LINK.eq(link))
            .fetchOne();
        return count != null && count.value1() != 0;
    }

    @Transactional
    public boolean saveLink(String link) {
        context.insertInto(LINKS, LINKS.LINK).values(link).execute();
        return true;
    }

    @Transactional
    public boolean updateContent(Long linkId, String update, Date date) {
        context.insertInto(CONTENT_BY_LINK)
            .set(CONTENT_BY_LINK.LINK_ID, linkId)
            .set(CONTENT_BY_LINK.CONTENT, update)
            .set(CONTENT_BY_LINK.UPDATED_AT, date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime())
            .onConflict(CONTENT_BY_LINK.LINK_ID)
            .doUpdate()
            .set(CONTENT_BY_LINK.CONTENT, update)
            .set(CONTENT_BY_LINK.UPDATED_AT, date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime())
            .execute();

        return true;
    }

    @Transactional
    public ListLinksResponse getAllByChatId(Long chatId) {
        var result = context.select(LINKS.LINK, LINKS.ID)
            .from(CHATS_TO_LINKS)
            .join(LINKS).on(LINKS.ID.eq(CHATS_TO_LINKS.LINK_ID))
            .where(CHATS_TO_LINKS.CHAT_ID.eq(chatId))
            .fetch();

        var res = result.map(r -> new LinkResponse(r.value2(), URI.create(r.value1())));
        return new ListLinksResponse(res, res.size());
    }

    @Transactional
    public Collection<String> getAllLinks() {
        return context.select(LINKS.LINK).from(LINKS).fetch(LINKS.LINK);
    }
}
