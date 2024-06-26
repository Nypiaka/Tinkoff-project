package edu.java.service;

import edu.java.dao.JdbcLinksDao;
import edu.java.dao.LinksDao;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JdbcLinksService extends AbstractLinksService {
    private final JdbcLinksDao linksDao;

    @Override
    protected LinksDao getDao() {
        return linksDao;
    }

}
