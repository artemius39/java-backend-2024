package edu.java.scrapper.configuration;

import edu.java.scrapper.client.ScrapperClient;
import edu.java.scrapper.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.repository.jdbc.JdbcUserRepository;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.LinkServiceImpl;
import edu.java.scrapper.service.Updater;
import edu.java.scrapper.service.UpdaterImpl;
import edu.java.scrapper.service.UserService;
import edu.java.scrapper.service.UserServiceImpl;
import edu.java.scrapper.service.updater.LinkUpdater;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-default-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {
    @Bean
    public LinkService jdbcLinkService(JdbcLinkRepository linkRepository, JdbcUserRepository userRepository) {
        return new LinkServiceImpl(linkRepository, userRepository);
    }

    @Bean
    public UserService jdbcUserService(JdbcUserRepository userRepository, JdbcLinkRepository linkRepository) {
        return new UserServiceImpl(userRepository, linkRepository);
    }

    @Bean
    public JdbcUserRepository jdbcUserRepository(JdbcClient jdbcClient) {
        return new JdbcUserRepository(jdbcClient);
    }

    @Bean
    public JdbcLinkRepository jdbcLinkRepository(JdbcClient jdbcClient) {
        return new JdbcLinkRepository(jdbcClient);
    }

    @Bean
    public Updater jdbcUpdater(
        JdbcLinkRepository linkRepository,
        List<LinkUpdater> updaters,
        ScrapperClient scrapperClient,
        ApplicationConfig applicationConfig
    ) {
        return new UpdaterImpl(updaters, linkRepository, scrapperClient, applicationConfig);
    }
}
