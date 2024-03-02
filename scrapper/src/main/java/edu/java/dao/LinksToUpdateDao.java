package edu.java.dao;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class LinksToUpdateDao {

    private final Map<String, String> links = new HashMap<>(Map.of(
        "github.com/nypiaka/itmo-projects",
        "",
        "https://stackoverflow.com/questions/74074986/how-to-change-springs-scheduled-fixeddelay-at-runtime",
        ""
    ));

    public String get(String s) {
        //TODO
        return links.get(s);
    }

    public void save(String link, String dto) {
        //TODO
        links.put(link, dto);
    }

    public Iterable<String> getAllLinks() {
        //TODO
        return links.keySet();
    }
}
