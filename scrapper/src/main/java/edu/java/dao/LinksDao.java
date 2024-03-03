package edu.java.dao;

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
        link = link.toLowerCase();
        links.putIfAbsent(id, new HashSet<>());
        if (content != null) {
            lastLinkUpdate.put(link, content);
        }
        links.get(id).add(link);
        return links.containsKey(id) && links.get(id).contains(link);
    }

    public boolean removeLink(Long id, String link) {
        link = link.toLowerCase();
        if (!containsLink(id, link)) {
            return false;
        }
        links.get(id).remove(link);
        return !links.get(id).contains(link);
    }

    public boolean containsLink(Long id, String link) {
        link = link.toLowerCase();
        return links.containsKey(id) && links.get(id).contains(link);
    }

    public boolean updateLink(List<Long> id, String link, String content) {
        link = link.toLowerCase();
        var success = true;
        for (var i : id) {
            success &= saveLink(i, link, content);
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
        link = link.toLowerCase();
        return lastLinkUpdate.get(link);
    }

    public boolean save(String link, String update) {
        link = link.toLowerCase();
        if (!lastLinkUpdate.containsKey(link) || !lastLinkUpdate.get(link).equals(update)) {
            lastLinkUpdate.put(link, update);
        }
        return lastLinkUpdate.containsKey(link);
    }

}
