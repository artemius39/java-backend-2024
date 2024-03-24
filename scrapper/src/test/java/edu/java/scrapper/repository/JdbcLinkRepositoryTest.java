package edu.java.scrapper.repository;

import edu.java.scrapper.repository.jdbc.JdbcLinkRepository;

public class JdbcLinkRepositoryTest extends LinkRepositoryTest {
    @Override
    protected LinkRepository getRepository() {
        return new JdbcLinkRepository(jdbcClient);
    }
}
