package edu.java.scrapper.repository;

import edu.java.scrapper.repository.jdbc.JdbcUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;

class JdbcUserRepositoryTest extends UserRepositoryTest {
    @Autowired
    private JdbcClient jdbcClient;

    @Override
    protected UserRepository getRepository() {
        return new JdbcUserRepository(jdbcClient);
    }
}
