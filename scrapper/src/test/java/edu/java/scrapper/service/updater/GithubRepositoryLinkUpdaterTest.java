package edu.java.scrapper.service.updater;

import edu.java.scrapper.client.GithubClient;
import edu.java.scrapper.dto.github.RepositoryResponse;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GithubRepositoryLinkUpdaterTest {

    @Test
    void validLinksAreSupported() {
        GithubClient client = mock(GithubClient.class);
        when(client.testRepositoryUrl("owner", "name"))
            .thenReturn(HttpStatus.valueOf(200));
        LinkUpdater updater = new GithubRepositoryLinkUpdater(client);

        boolean supports = updater.supports(URI.create("https://github.com/owner/name"));

        assertThat(supports).isTrue();
    }

    @Test
    void nonExistingReposAreNotSupported() {
        GithubClient client = mock(GithubClient.class);
        when(client.testRepositoryUrl("owner", "name"))
            .thenReturn(HttpStatus.valueOf(404));
        LinkUpdater updater = new GithubRepositoryLinkUpdater(client);

        boolean supports = updater.supports(URI.create("https://github.com/owner/name"));

        assertThat(supports).isFalse();
    }

    @Test
    void nonGithubLinksAreNotSupported() {
        LinkUpdater updater = new GithubRepositoryLinkUpdater(mock(GithubClient.class));

        boolean supports = updater.supports(URI.create("invalid"));

        assertThat(supports).isFalse();
    }

    @Test
    void emptyMessageIsReturnedOnNoUpdate() {
        OffsetDateTime now = OffsetDateTime.now();
        GithubClient client = mock(GithubClient.class);
        when(client.testRepositoryUrl("owner", "name"))
            .thenReturn(HttpStatus.valueOf(200));
        when(client.getLastUpdateTime("owner", "name"))
            .thenReturn(new RepositoryResponse(now));
        LinkUpdater updater = new GithubRepositoryLinkUpdater(client);

        Optional<String> message = updater.tryUpdate(URI.create("https://github.com/owner/name"), now);

        assertThat(message).isEmpty();
    }

    @Test
    void emptyMessageIsReturnedOnUnsupportedLink() {
        OffsetDateTime yesterday = OffsetDateTime.now().minusDays(1);
        LinkUpdater updater = new GithubRepositoryLinkUpdater(null);

        Optional<String> message = updater.tryUpdate(URI.create("https://github.com/owner/name/commits"), yesterday);

        assertThat(message).isEmpty();
    }

    @Test
    void nonEmptyMessageIsReturnedOnUpdate() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime yesterday = now.minusDays(1);

        GithubClient client = mock(GithubClient.class);
        when(client.testRepositoryUrl("owner", "name"))
            .thenReturn(HttpStatus.valueOf(200));
        when(client.getLastUpdateTime("owner", "name"))
            .thenReturn(new RepositoryResponse(now));

        LinkUpdater updater = new GithubRepositoryLinkUpdater(client);

        Optional<String> message = updater.tryUpdate(URI.create("https://github.com/owner/name"), yesterday);

        assertThat(message).isNotEmpty();
    }
}
