package edu.java.dao;

import java.sql.SQLException;

public class JooqDaoTest extends AbstractDaoTest {
    @Override
    public LinksDao getDao() throws SQLException {
        return new JooqLinksDao(DATA);
    }
}
