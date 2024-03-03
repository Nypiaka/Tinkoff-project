package edu.java.bot.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class LinksDao {

    private final Map<Long, HashSet<String>> links = new HashMap<>();

    private final Map<String, String> lastLinkUpdate = new HashMap<>();

    public List<String> getList(Long id) {
        if (!links.containsKey(id)) {
            return List.of();
        }
        return links.get(id).stream().toList();
    }

    public boolean saveLink(Long id, String link, String content) {
        var curLink = link.toLowerCase();
        links.putIfAbsent(id, new HashSet<>());
        if (content != null) {
            lastLinkUpdate.put(curLink, content);
        }
        links.get(id).add(curLink);
        return links.containsKey(id) && links.get(id).contains(curLink);
    }

    public boolean removeLink(Long id, String link) {
        var curLink = link.toLowerCase();
        if (!containsLink(id, curLink)) {
            return false;
        }
        links.get(id).remove(curLink);
        return !links.get(id).contains(curLink);
    }

    public boolean containsLink(Long id, String link) {
        var curLink = link.toLowerCase();
        return links.containsKey(id) && links.get(id).contains(curLink);
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
        if (links.containsKey(id)) {
            return false;
        }
        links.put(id, new HashSet<>());
        return links.containsKey(id);
    }

    public boolean removeChat(Long id) {
        if (!links.containsKey(id)) {
            return false;
        }
        links.remove(id);
        return !links.containsKey(id);
    }

    public boolean containsChat(Long id) {
        return links.containsKey(id);
    }

    public Collection<String> getAllLinks() {
        return lastLinkUpdate.keySet();
    }

    public String getLastUpdate(String link) {
        var curLink = link.toLowerCase();
        return lastLinkUpdate.get(curLink);
    }

    public boolean save(String link, String update) {
        var curLink = link.toLowerCase();
        if (!lastLinkUpdate.containsKey(curLink) || !lastLinkUpdate.get(curLink).equals(update)) {
            lastLinkUpdate.put(curLink, update);
        }
        return lastLinkUpdate.containsKey(curLink);
    }

}
