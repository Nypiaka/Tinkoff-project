package edu.java.dao;

public class JdbcDaoTest extends AbstractDaoTest {
    @Override
    public LinksDao getDao() {
        return new JdbcLinksDao(DATA);
    }
}
