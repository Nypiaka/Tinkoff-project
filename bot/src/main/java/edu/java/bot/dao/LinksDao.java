package edu.java.bot.dao;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class LinksDao {
    public List<String> getList(Long id) {
        //TODO
        return List.of("Empty now. But someday it won't be empty.");
    }

    public boolean saveLink(Long id, String link) {
        //TODO
        return false;
    }

    public boolean removeLink(Long id, String s) {
        //TODO
        return false;
    }
}
