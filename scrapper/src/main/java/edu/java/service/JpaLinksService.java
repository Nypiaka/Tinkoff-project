package edu.java.service;

import edu.java.dao.domain.ChatsToLinks;
import edu.java.dao.domain.ContentByLink;
import edu.java.dao.domain.Link;
import edu.java.dao.repository.JpaChatRepository;
import edu.java.dao.repository.JpaContentRepository;
import edu.java.dao.repository.JpaLinksRepository;
import edu.java.scheduler.LinkUpdaterScheduler;
import edu.java.utils.dto.LinkResponse;
import edu.java.utils.dto.ListLinksResponse;
import java.net.URI;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JpaLinksService implements LinksService {
    JpaLinksRepository jpaLinksRepository;
    JpaChatRepository jpaChatRepository;
    JpaContentRepository jpaContentRepository;

    LinkUpdaterScheduler linkUpdaterScheduler;

    @Override
    public boolean saveLinkInChat(Long chatId, String link) {
        if (!jpaLinksRepository.existsByLink(link)) {
            jpaLinksRepository.save(new Link(link));
            var linkId = jpaLinksRepository.findByLink(link).orElseThrow().getId();
            jpaContentRepository.save(new ContentByLink(linkId, "", new Date()));
            linkUpdaterScheduler.forceUpdate(link);
        }
        var linkId = jpaLinksRepository.findByLink(link).orElseThrow().getId();
        var alreadyExists = jpaChatRepository.existsByChatIdAndLinkId(chatId, linkId);
        if (alreadyExists) {
            return false;
        }
        jpaChatRepository.save(new ChatsToLinks(chatId, linkId));
        return jpaChatRepository.existsByChatId(chatId);
    }

    @Override
    public boolean removeLinkFromChat(Long chatId, String link) {
        var foundLink = jpaLinksRepository.findByLink(link);
        if (foundLink.isEmpty()) {
            return false;
        }
        var chatContainsLink = jpaChatRepository.existsByChatIdAndLinkId(chatId, foundLink.orElseThrow().getId());
        if (!chatContainsLink) {
            return false;
        }
        jpaChatRepository.delete(new ChatsToLinks(chatId, foundLink.orElseThrow().getId()));
        return true;
    }

    @Override
    public boolean removeChat(Long chatId) {
        if (!jpaChatRepository.existsByChatId(chatId)) {
            return false;
        }
        jpaChatRepository.deleteAllByChatId(chatId);
        return true;
    }

    @Override
    public boolean registerChat(Long chatId) {
        return true;
    }

    @Override
    public ListLinksResponse getAllByChatId(Long id) {
        var links = jpaChatRepository.findAllByChatId(id);
        var list = links.stream().map(l -> {
            var link = jpaLinksRepository.findById(l.getLinkId()).orElseThrow();
            return new LinkResponse(link.getId(), URI.create(link.getLink()));
        }).toList();
        return new ListLinksResponse(list, list.size());
    }

    @Override
    public boolean containsChatAndLink(Long chatId, String link) {
        return jpaChatRepository.existsByChatIdAndLinkId(chatId, getId(link));
    }

    @Override
    public boolean containsChat(Long id) {
        return jpaChatRepository.existsByChatId(id);
    }

    @Override
    public Iterable<String> getAllLinksForInterval(Integer updateTime) {
        return jpaContentRepository.getAllLinksForInterval(updateTime);
    }

    @Override
    public long getId(String link) {
        return jpaLinksRepository.findByLink(link).orElseThrow().getId();
    }

    @Override
    public String getLastUpdate(String link) {
        return jpaContentRepository.findByLinkId(jpaLinksRepository.findByLink(link).orElseThrow().getId())
            .orElseThrow().getContent();
    }

    @Override
    public List<Long> getChatsByLink(String link) {
        return jpaChatRepository.findAllByLinkId(jpaLinksRepository.findByLink(link).orElseThrow().getId()).stream()
            .map(ChatsToLinks::getChatId).toList();
    }

    @Override
    public void update(String s, String update) {
        jpaContentRepository.save(new ContentByLink(
            jpaLinksRepository.findByLink(s).orElseThrow().getId(),
            update,
            new Date()
        ));
    }

}
