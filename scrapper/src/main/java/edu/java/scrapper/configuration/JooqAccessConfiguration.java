package edu.java.scrapper.configuration;

import edu.java.scrapper.client.ScrapperClient;
import edu.java.scrapper.repository.jooq.JooqLinkRepository;
import edu.java.scrapper.repository.jooq.JooqUserRepository;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.LinkServiceImpl;
import edu.java.scrapper.service.Updater;
import edu.java.scrapper.service.UpdaterImpl;
import edu.java.scrapper.service.UserService;
import edu.java.scrapper.service.UserServiceImpl;
import edu.java.scrapper.service.updater.LinkUpdater;
import java.util.List;
import org.jooq.DSLContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-default-type", havingValue = "jooq")
public class JooqAccessConfiguration {
    @Bean
    public LinkService jooqLinkService(JooqLinkRepository linkRepository, JooqUserRepository userRepository) {
        return new LinkServiceImpl(linkRepository, userRepository);
    }

    @Bean
    public UserService jooqUserService(JooqUserRepository userRepository, JooqLinkRepository linkRepository) {
        return new UserServiceImpl(userRepository, linkRepository);
    }

    @Bean
    public JooqUserRepository jooqUserRepository(DSLContext dslContext) {
        return new JooqUserRepository(dslContext);
    }

    @Bean
    public JooqLinkRepository jooqLinkRepository(DSLContext dslContext) {
        return new JooqLinkRepository(dslContext);
    }

    @Bean
    public Updater jooqUpdater(
        JooqLinkRepository linkRepository,
        List<LinkUpdater> updaters,
        ScrapperClient scrapperClient,
        ApplicationConfig applicationConfig
    ) {
        return new UpdaterImpl(updaters, linkRepository, scrapperClient, applicationConfig);
    }
}
