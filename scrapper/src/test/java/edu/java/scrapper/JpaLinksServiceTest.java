package edu.java.scrapper;

import edu.java.dao.domain.ChatsToLinks;
import edu.java.dao.domain.ContentByLink;
import edu.java.dao.domain.Link;
import edu.java.dao.repository.JpaChatRepository;
import edu.java.dao.repository.JpaContentRepository;
import edu.java.dao.repository.JpaLinksRepository;
import edu.java.scheduler.LinkUpdaterScheduler;
import edu.java.service.JpaLinksService;
import edu.java.utils.dto.LinkResponse;
import edu.java.utils.dto.ListLinksResponse;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JpaLinksServiceTest {

    private JpaLinksRepository linksRepository;
    private JpaChatRepository chatRepository;
    private JpaContentRepository contentRepository;
    private LinkUpdaterScheduler scheduler;
    private JpaLinksService service;

    @BeforeEach
    public void setup() {
        linksRepository = Mockito.mock(JpaLinksRepository.class);
        chatRepository = Mockito.mock(JpaChatRepository.class);
        contentRepository = Mockito.mock(JpaContentRepository.class);
        scheduler = Mockito.mock(LinkUpdaterScheduler.class);
        service = new JpaLinksService(linksRepository, chatRepository, contentRepository, scheduler);
    }

    @Test
    public void testGetList() {
        var testId = 10L;
        when(chatRepository.findAllByChatId(testId)).thenReturn(
            List.of(new ChatsToLinks(1L, 2L))
        );
        var resLink = "some link";
        when(linksRepository.findById(2L)).thenReturn(Optional.of(new Link(2L, resLink)));
        var res = service.getList(testId);
        Assertions.assertEquals(res, List.of(resLink));
    }

    @Test
    public void testSaveLinkAndUpdate() {
        Long chatId = 1L;
        String link = "http://example.com";
        String content = "Some content";
        var was = new AtomicBoolean(false);
        when(linksRepository.findByLink(link)).thenReturn(
            Optional.empty()).thenReturn(Optional.of(new Link(1L, link)));
        when(linksRepository.save(new Link(link))).then(i -> {
            was.set(true);
            return null;
        });
        var savedContent = new AtomicBoolean(false);
        when(contentRepository.save(new ContentByLink(1L, content, any()))).then(i -> {
            savedContent.set(true);
            return null;
        });
        var savedChat = new AtomicBoolean(false);
        when(chatRepository.save(new ChatsToLinks(chatId, 1L))).then(i -> {
            savedChat.set(true);
            return null;
        });
        boolean result = service.saveLinkAndUpdate(chatId, link, content);
        Assertions.assertTrue(result);
        Assertions.assertTrue(was.get());
        Assertions.assertTrue(savedContent.get());
        Assertions.assertTrue(savedChat.get());
    }

    @Test
    public void testRemoveLink() {
        Long chatId = 1L;
        String link = "http://example.com";
        when(linksRepository.findByLink(link)).thenReturn(Optional.of(new Link(1L, link)));
        boolean result = service.removeLink(chatId, link);
        verify(chatRepository).delete(new ChatsToLinks(chatId, 1L));
        Assertions.assertTrue(result);
    }

    @Test
    public void testContainsLink() {
        Long chatId = 1L;
        String link = "http://example.com";
        when(linksRepository.findByLink(link)).thenReturn(Optional.of(new Link(1L, link)));
        when(chatRepository.existsByChatIdAndLinkId(chatId, 1L)).thenReturn(true);
        boolean result = service.containsLink(chatId, link);
        verify(chatRepository).existsByChatIdAndLinkId(chatId, 1L);
        Assertions.assertTrue(result);
    }

    @Test
    public void testUpdateLink() {
        Long chatId = 1L;
        String link = "http://example.com";
        String content = "Some content";
        List<Long> ids = Arrays.asList(1L, 2L);
        when(linksRepository.findByLink(link)).thenReturn(Optional.of(new Link(1L, link)));
        boolean result = service.updateLink(ids, link, content);
        for (Long ignored : ids) {
            verify(chatRepository).save(new ChatsToLinks(chatId, 1L));
        }
        Assertions.assertTrue(result);
    }

    @Test
    public void testRemoveChat() {
        Long chatId = 1L;
        when(chatRepository.existsByChatId(chatId)).thenReturn(true);
        boolean result = service.removeChat(chatId);
        verify(chatRepository).deleteByChatId(chatId);
        Assertions.assertTrue(result);
    }

    @Test
    public void testContainsChat() {
        Long chatId = 1L;
        when(chatRepository.existsByChatId(chatId)).thenReturn(true);
        boolean result = service.containsChat(chatId);
        verify(chatRepository).existsByChatId(chatId);
        Assertions.assertTrue(result);
    }

    @Test
    public void testGetAllLinks() {
        List<Link> links = Arrays.asList(new Link(1L, "http://example.com"), new Link(2L, "http://example.com"));
        when(linksRepository.findAll()).thenReturn(links);
        Collection<String> result = service.getAllLinks();
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains("http://example.com"));
    }

    @Test
    public void testGetChatsByLink() {
        String link = "http://example.com";
        Long id = 1L;
        when(linksRepository.findByLink(link)).thenReturn(Optional.of(new Link(id, link)));
        when(chatRepository.findAllByLinkId(id)).thenReturn(List.of(new ChatsToLinks(1L, id)));
        List<Long> result = service.getChatsByLink(link);
        Assertions.assertEquals(1, result.size());
        Assertions.assertTrue(result.contains(1L));
    }

    @Test
    public void testGetId() {
        String link = "http://example.com";
        Long id = 1L;
        when(linksRepository.findByLink(link)).thenReturn(Optional.of(new Link(id, link)));
        Long result = service.getId(link);
        Assertions.assertEquals(id, result);
    }

    @Test
    public void testGetLastUpdate() {
        String link = "http://example.com";
        String update = "Some content";
        Long id = 1L;
        when(contentRepository.findByLinkId(id)).thenReturn(Optional.of(new ContentByLink(id, update, new Date())));
        when(linksRepository.findByLink(link)).thenReturn(Optional.of(new Link(id, link)));
        String result = service.getLastUpdate(link);
        Assertions.assertEquals(update, result);
    }

    @Test
    public void testGetLinksIdsByChatId() {
        Long chatId = 1L;
        List<ChatsToLinks> chats = Arrays.asList(new ChatsToLinks(1L, 1L), new ChatsToLinks(2L, 2L));
        when(chatRepository.findAllByChatId(chatId)).thenReturn(chats);
        List<Long> result = service.getLinksIdsByChatId(chatId);
        Assertions.assertEquals(2, result.size());
        Assertions.assertTrue(result.contains(1L));
        Assertions.assertTrue(result.contains(2L));
    }

    @Test
    public void testGetAllByChatId() {
        Long chatId = 1L;
        List<ChatsToLinks> chats = Arrays.asList(new ChatsToLinks(1L, 1L), new ChatsToLinks(2L, 2L));
        when(chatRepository.findAllByChatId(chatId)).thenReturn(chats);
        when(linksRepository.findById(1L)).thenReturn(Optional.of(new Link(1L, "http://example.com")));
        when(linksRepository.findById(2L)).thenReturn(Optional.of(new Link(2L, "http://example.com")));
        ListLinksResponse result = service.getAllByChatId(chatId);
        Assertions.assertEquals(2, result.getLinks().size());
        Assertions.assertTrue(result.getLinks().contains(new LinkResponse(1L, URI.create("http://example.com"))));
        Assertions.assertTrue(result.getLinks().contains(new LinkResponse(2L, URI.create("http://example.com"))));
    }
}
