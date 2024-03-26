package edu.java.scrapper.service.jpa;

import edu.java.scrapper.client.ScrapperClient;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.dto.bot.LinkUpdateRequest;
import edu.java.scrapper.model.jpa.Link;
import edu.java.scrapper.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.repository.jpa.JpaUserRepository;
import edu.java.scrapper.service.JpaUpdater;
import edu.java.scrapper.service.Updater;
import edu.java.scrapper.service.updater.LinkUpdater;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaUpdaterTest {
    @Test
    void updateIsSentIfLinkHasUpdate() {
        URI url = URI.create("example.com");
        Link link = new Link();
        link.setId(1L);
        link.setUrl("example.com");
        link.setUpdatedAt(OffsetDateTime.MIN);

        LinkUpdater updater1 = mock(LinkUpdater.class);
        when(updater1.tryUpdate(eq(url), any()))
            .thenReturn(Optional.empty());
        LinkUpdater updater2 = mock(LinkUpdater.class);
        when(updater2.tryUpdate(eq(url), any()))
            .thenReturn(Optional.of("Link updated"));
        JpaLinkRepository linkRepository = mock(JpaLinkRepository.class);
        when(linkRepository.findLinksByUpdatedAtBefore(any()))
            .thenReturn(List.of(link));
        JpaUserRepository userRepository = mock(JpaUserRepository.class);
        when(userRepository.findUsersByLink(1L))
            .thenReturn(List.of(1L, 2L, 3L));
        ScrapperClient client = mock(ScrapperClient.class);
        ApplicationConfig config = new ApplicationConfig(null, Duration.of(1, ChronoUnit.MINUTES), null);

        Updater updater = new JpaUpdater(
            List.of(updater1, updater2),
            linkRepository,
            client,
            config,
            userRepository
        );

        int result = updater.update();

        assertThat(result).isOne();
        verify(client).sendUpdate(new LinkUpdateRequest(1L, url, "Link updated", List.of(1L, 2L, 3L)));
        verify(linkRepository).save(any());
    }

    @Test
    void updateIsSentIfLinkDoesNotHaveUpdate() {
        URI url = URI.create("example.com");
        Link link = new Link();
        link.setId(1L);
        link.setUrl("example.com");
        link.setUpdatedAt(OffsetDateTime.MIN);

        LinkUpdater updater1 = mock(LinkUpdater.class);
        when(updater1.tryUpdate(eq(url), any()))
            .thenReturn(Optional.empty());
        LinkUpdater updater2 = mock(LinkUpdater.class);
        when(updater2.tryUpdate(eq(url), any()))
            .thenReturn(Optional.empty());
        JpaLinkRepository linkRepository = mock(JpaLinkRepository.class);
        when(linkRepository.findLinksByUpdatedAtBefore(any()))
            .thenReturn(List.of(link));
        JpaUserRepository userRepository = mock(JpaUserRepository.class);
        when(userRepository.findUsersByLink(1L))
            .thenReturn(List.of(1L, 2L, 3L));
        ScrapperClient client = mock(ScrapperClient.class);
        ApplicationConfig config = new ApplicationConfig(null, Duration.of(1, ChronoUnit.MINUTES), null);

        Updater updater = new JpaUpdater(
            List.of(updater1, updater2),
            linkRepository,
            client,
            config,
            userRepository
        );

        int result = updater.update();

        assertThat(result).isZero();
        verify(client, never()).sendUpdate(any());
        verify(linkRepository, never()).save(any());
    }
}
