package edu.java.service;

import edu.java.dao.JooqLinksDao;
import edu.java.dao.LinksDao;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JooqLinksService extends AbstractLinksService {
    private final JooqLinksDao linksDao;

    @Override
    protected LinksDao getDao() {
        return linksDao;
    }
}
