package edu.java.scrapper.service;

import edu.java.scrapper.client.ScrapperClient;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.dto.bot.LinkUpdateRequest;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.repository.LinkRepository;
import edu.java.scrapper.service.updater.LinkUpdater;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UpdaterImpl implements Updater {
    private final List<LinkUpdater> updaters;
    private final LinkRepository linkRepository;
    private final ScrapperClient scrapperClient;
    private final ApplicationConfig applicationConfig;

    @Override
    public int update() {
        int updateCount = 0;
        Collection<Link> linksToUpdate = linkRepository.findByLastUpdateTime(applicationConfig.updateInterval());
        for (Link link : linksToUpdate) {
            for (LinkUpdater updater : updaters) {
                updateCount += updateLink(link, updater);
            }
        }
        return updateCount;
    }

    private int updateLink(Link link, LinkUpdater updater) {
        Optional<String> message = updater.tryUpdate(link.getUrl(), OffsetDateTime.now());
        if (message.isEmpty()) {
            return 0;
        }
        scrapperClient.sendUpdate(new LinkUpdateRequest(
            link.getId(),
            link.getUrl(),
            message.get(),
            linkRepository.findUserIdsByLinkId(link.getId())
        ));
        linkRepository.update(link, OffsetDateTime.now());
        return 1;
    }
}
