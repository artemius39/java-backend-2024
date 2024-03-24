package edu.java.scrapper.repository;

import edu.java.scrapper.repository.jooq.JooqLinkRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;

public class JooqLinkRepositoryTest extends LinkRepositoryTest {
    @Autowired
    private DSLContext dslContext;

    @Override
    protected LinkRepository getRepository() {
        return new JooqLinkRepository(dslContext);
    }
}
