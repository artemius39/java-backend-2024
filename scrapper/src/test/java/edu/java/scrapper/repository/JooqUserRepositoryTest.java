package edu.java.scrapper.repository;

import edu.java.scrapper.repository.jooq.JooqUserRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;

class JooqUserRepositoryTest extends UserRepositoryTest {
    @Autowired
    private DSLContext dslContext;

    @Override
    protected UserRepository getRepository() {
        return new JooqUserRepository(dslContext);
    }
}
