package edu.java.scrapper;

import edu.java.service.LinksService;
import jakarta.transaction.Transactional;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public abstract class AbstractServiceTest extends IntegrationTest {
    protected abstract LinksService getService();

    @Test @Transactional
    public void testGetList() throws SQLException {
        var service = getService();
        var id = 1L;
        var link1 = "https://example.com/1";
        var link2 = "https://example.com/2";
        var link3 = "https://example.com/3";
        var links = List.of(link1, link2, link3);
        saveLinks(id, links);
        var retrievedLinks = service.getAllByChatId(id).getLinks().stream().map(lr -> lr.getUrl().toString()).toList();
        Assertions.assertEquals(new HashSet<>(links), new HashSet<>(retrievedLinks));
    }

    @Test @Transactional
    public void testRemoveChat() {
        var service = getService();
        var id = 1L;
        service.registerChat(id);
        service.removeChat(id);
        var containsChat = service.containsChat(id);
        Assertions.assertFalse(containsChat);
    }

    @Test @Transactional
    public void testGetLastUpdate() {
        var service = getService();
        var link = "https://example.com/test";
        var update1 = "This is the first update for " + link;
        var update2 = "This is the second update for " + link;
        saveLinkInChat(1L, link);
        service.update(link, update1);
        service.update(link, update2);
        var retrievedUpdate = service.getLastUpdate(link);
        Assertions.assertEquals(update2, retrievedUpdate);
    }

    @Test @Transactional
    public void testSave() {
        var service = getService();
        var link = "https://example.com/test";
        var update = "This is the update for " + link;
        saveLinkInChat(1L, link);
        service.update(link, update);
        var retrievedUpdate = service.getLastUpdate(link);
        Assertions.assertEquals(update, retrievedUpdate);
    }

    @Test @Transactional
    public void testSaveLinkInChat() {
        var service = getService();
        var link = "https://example.com/test";
        var result = service.saveLinkInChat(1L, link);
        Assertions.assertTrue(result);
    }

    @Test @Transactional
    public void testRemoveLinkFromChat() {
        var service = getService();
        var link = "https://example.com/test";
        saveLinkInChat(1L, link);
        var result = service.removeLinkFromChat(1L, link);
        Assertions.assertTrue(result);
        Assertions.assertFalse(containsLinkInChat(1L, link));
    }

    protected abstract boolean containsLinkInChat(Long id, String link);

    @Test @Transactional
    public void testGetChatsByLink() {
        var service = getService();
        var link = "https://example.com/test";
        var chat1 = 1L;
        var chat2 = 2L;
        saveLinkInChat(chat1, link);
        saveLinkInChat(chat2, link);

        var chats = service.getChatsByLink(link);

        Assertions.assertEquals(2, chats.size());
        Assertions.assertTrue(chats.contains(chat1));
        Assertions.assertTrue(chats.contains(chat2));
    }

    @Test @Transactional
    public void testUpdate() {
        var service = getService();
        var link = "https://example.com/test";
        var update = "This is the update for " + link;

        saveLinkInChat(1L, link);
        service.update(link, update);

        var retrievedUpdate = service.getLastUpdate(link);
        Assertions.assertEquals(update, retrievedUpdate);
    }

    protected abstract void saveLinks(Long id, List<String> links);

    protected abstract void saveLinkInChat(Long id, String link);
}
