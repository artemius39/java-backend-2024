package edu.java.scrapper.configuration;

import edu.java.scrapper.client.ScrapperClient;
import edu.java.scrapper.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.repository.jpa.JpaUserRepository;
import edu.java.scrapper.service.JpaLinkService;
import edu.java.scrapper.service.JpaUpdater;
import edu.java.scrapper.service.JpaUserService;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.Updater;
import edu.java.scrapper.service.UserService;
import edu.java.scrapper.service.updater.LinkUpdater;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-default-type", havingValue = "jpa")
public class JpaAccessConfiguration {
    @Bean
    public LinkService jpaLinkService(JpaLinkRepository linkRepository, JpaUserRepository userRepository) {
        return new JpaLinkService(linkRepository, userRepository);
    }

    @Bean
    public UserService jpaUserService(JpaUserRepository userRepository, JpaLinkRepository linkRepository) {
        return new JpaUserService(userRepository, linkRepository);
    }

    @Bean
    public Updater jpaUpdater(
        JpaLinkRepository linkRepository,
        JpaUserRepository userRepository,
        List<LinkUpdater> updaters,
        ScrapperClient scrapperClient,
        ApplicationConfig applicationConfig
    ) {
        return new JpaUpdater(updaters, linkRepository, scrapperClient, applicationConfig, userRepository);
    }
}
