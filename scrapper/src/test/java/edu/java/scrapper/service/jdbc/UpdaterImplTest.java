package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.client.ScrapperClient;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.dto.bot.LinkUpdateRequest;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.service.Updater;
import edu.java.scrapper.service.UpdaterImpl;
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

class UpdaterImplTest {
    @Test
    void updateIsSentIfLinkHasUpdate() {
        URI url = URI.create("example.com");
        Link link = new Link(1L, url, OffsetDateTime.MIN);

        LinkUpdater updater1 = mock(LinkUpdater.class);
        when(updater1.tryUpdate(any(), any())).thenReturn(Optional.empty());
        LinkUpdater updater2 = mock(LinkUpdater.class);
        when(updater2.tryUpdate(any(), any())).thenReturn(Optional.of("Link updated"));

        JdbcLinkRepository linkRepository = mock(JdbcLinkRepository.class);
        when(linkRepository.findByLastUpdateTime(any()))
            .thenReturn(List.of(link));
        when(linkRepository.findUserIdsByLinkId(1L))
            .thenReturn(List.of(1L, 2L, 3L));

        ScrapperClient client = mock(ScrapperClient.class);

        ApplicationConfig config = new ApplicationConfig(null, Duration.of(1, ChronoUnit.MINUTES));

        Updater updater = new UpdaterImpl(List.of(updater1, updater2), linkRepository, client, config);

        int result = updater.update();

        assertThat(result).isOne();
        verify(client).sendUpdate(new LinkUpdateRequest(1L, url, "Link updated", List.of(1L, 2L, 3L)));
        verify(linkRepository).update(eq(link), any());
    }

    @Test
    void updateIsNotSentIfLinkDoesNotHaveUpdate() {
        URI url = URI.create("example.com");
        Link link = new Link(1L, url, OffsetDateTime.MIN);

        LinkUpdater updater1 = mock(LinkUpdater.class);
        when(updater1.tryUpdate(any(), any())).thenReturn(Optional.empty());
        LinkUpdater updater2 = mock(LinkUpdater.class);
        when(updater2.tryUpdate(any(), any())).thenReturn(Optional.empty());

        JdbcLinkRepository linkRepository = mock(JdbcLinkRepository.class);
        when(linkRepository.findByLastUpdateTime(any())).thenReturn(List.of(link));

        ScrapperClient client = mock(ScrapperClient.class);

        ApplicationConfig config = new ApplicationConfig(null, Duration.of(1, ChronoUnit.MINUTES));

        Updater updater = new UpdaterImpl(List.of(updater1, updater2), linkRepository, client, config);

        int result = updater.update();

        assertThat(result).isZero();
        verify(client, never()).sendUpdate(any());
    }
}
