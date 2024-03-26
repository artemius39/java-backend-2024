package edu.java.scrapper.service;

import edu.java.scrapper.client.ScrapperClient;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.dto.bot.LinkUpdateRequest;
import edu.java.scrapper.model.jpa.Link;
import edu.java.scrapper.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.repository.jpa.JpaUserRepository;
import edu.java.scrapper.service.updater.LinkUpdater;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JpaUpdater implements Updater {
    private final List<LinkUpdater> updaters;
    private final JpaLinkRepository linkRepository;
    private final ScrapperClient scrapperClient;
    private final ApplicationConfig applicationConfig;
    private final JpaUserRepository userRepository;

    @Override
    public int update() {
        int updateCount = 0;
        OffsetDateTime time = OffsetDateTime.now().minus(applicationConfig.updateInterval());
        Collection<Link> linksToUpdate = linkRepository.findLinksByUpdatedAtBefore(time);
        for (Link link : linksToUpdate) {
            for (LinkUpdater updater : updaters) {
                updateCount += updateLink(link, updater);
            }
        }
        return updateCount;
    }

    private int updateLink(Link link, LinkUpdater updater) {
        Optional<String> message = updater.tryUpdate(URI.create(link.getUrl()), OffsetDateTime.now());
        if (message.isEmpty()) {
            return 0;
        }
        scrapperClient.sendUpdate(new LinkUpdateRequest(
            link.getId(),
            URI.create(link.getUrl()),
            message.get(),
            userRepository.findUsersByLink(link.getId())
        ));
        link.setUpdatedAt(OffsetDateTime.now());
        linkRepository.save(link);
        return 1;
    }
}
