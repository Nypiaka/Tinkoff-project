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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;

@AllArgsConstructor
public class JpaLinksService implements LinksService {
    JpaLinksRepository jpaLinksRepository;
    JpaChatRepository jpaChatRepository;
    JpaContentRepository jpaContentRepository;

    LinkUpdaterScheduler linkUpdaterScheduler;

    @Override
    public List<String> getList(Long id) {
        return jpaChatRepository.findAllByChatId(id).stream()
            .map(cl -> jpaLinksRepository.findById(cl.getLinkId()).orElseThrow().getLink())
            .toList();
    }

    @Override
    public boolean saveLinkAndUpdate(Long id, String link, String content) {
        try {
            if (jpaLinksRepository.findByLink(link).isEmpty()) {
                jpaLinksRepository.save(new Link(link));
            }
            var curLink = jpaLinksRepository.findByLink(link).orElseThrow();
            jpaContentRepository.save(new ContentByLink(curLink.getId(), content, new Date()));
            jpaChatRepository.save(new ChatsToLinks(id, getId(curLink.getLink())));
            linkUpdaterScheduler.forceUpdate(link);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean removeLink(Long id, String link) {
        try {
            var curLink = jpaLinksRepository.findByLink(link);
            jpaChatRepository.delete(new ChatsToLinks(id, curLink.orElseThrow().getId()));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean containsLink(Long id, String link) {
        return jpaChatRepository.existsByChatIdAndLinkId(id, jpaLinksRepository.findByLink(link).orElseThrow().getId());
    }

    @Override
    public boolean updateLink(List<Long> ids, String link, String content) {
        var res = true;
        for (var id : ids) {
            res &= saveLinkAndUpdate(id, link, content);
        }
        return res;
    }

    @Override
    public boolean registerChat(Long id) {
        return true;
    }

    @Override
    public boolean removeChat(Long id) {
        if (jpaChatRepository.existsByChatId(id)) {
            jpaChatRepository.deleteByChatId(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean containsChat(Long id) {
        return jpaChatRepository.existsByChatId(id);
    }

    @Override
    public Collection<String> getAllLinks() {
        return jpaLinksRepository.findAll().stream().map(Link::getLink).toList();
    }

    @Override
    public List<Long> getChatsByLink(String link) {
        return jpaChatRepository.findAllByLinkId(jpaLinksRepository.findByLink(link).orElseThrow().getId()).stream()
            .map(
                ChatsToLinks::getChatId
            )
            .toList();
    }

    @Override
    public long getId(String link) {
        return jpaLinksRepository.findByLink(link).orElseThrow().getId();
    }

    @Override
    public String getLastUpdate(String link) {
        return jpaContentRepository.findByLinkId(getId(link)).orElseThrow().getContent();
    }

    @Override
    public void saveLinkAndUpdate(String link, String update) {
        var linkToWork = link.toLowerCase();
        try {
            if (!jpaLinksRepository.existsById(jpaLinksRepository.findByLink(linkToWork).orElseThrow().getId())) {
                jpaLinksRepository.save(jpaLinksRepository.findByLink(linkToWork).orElseThrow());
            }
        } catch (DuplicateKeyException ignored) {
        } finally {
            jpaContentRepository.save(new ContentByLink(
                jpaLinksRepository.findByLink(linkToWork).orElseThrow().getId(),
                update,
                new Date()
            ));
        }
    }

    @Override
    public List<Long> getLinksIdsByChatId(Long chatId) {
        return jpaChatRepository.findAllByChatId(chatId).stream().map(ChatsToLinks::getLinkId).toList();
    }

    @Override
    public ListLinksResponse getAllByChatId(Long chatId) {
        var found = jpaChatRepository.findAllByChatId(chatId).stream().map(
            cl -> new LinkResponse(
                cl.getLinkId(),
                URI.create(jpaLinksRepository.findById(cl.getLinkId()).orElseThrow().getLink())
            )).toList();
        return new ListLinksResponse(found, found.size());
    }

    @Override
    public List<String> getAllLinksForInterval(int i) {
        return jpaContentRepository.getAllLinksForInterval(i);
    }
}
