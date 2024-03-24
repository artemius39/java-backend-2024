package edu.java.scrapper.service.updater;

import edu.java.scrapper.client.GithubClient;
import edu.java.scrapper.dto.github.CommitsResponse;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GithubCommitsLinkUpdaterTest {
    private static final DateTimeFormatter ISO8601_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    @Test
    void validLinksAreSupported() {
        GithubClient client = mock(GithubClient.class);
        when(client.testCommitsUrl("owner", "name"))
            .thenReturn(HttpStatus.valueOf(200));
        LinkUpdater updater = new GithubCommitsLinkUpdater(client);

        boolean supports = updater.supports(URI.create("https://github.com/owner/name/commits"));

        assertThat(supports).isTrue();
    }

    @Test
    void nonCommitLinksAreNotSupported() {
        LinkUpdater updater = new GithubCommitsLinkUpdater(mock(GithubClient.class));

        boolean supports = updater.supports(URI.create("https://github.com/repo/owner"));

        assertThat(supports).isFalse();
    }

    @Test
    void nonExistingReposAreNotSupported() {
        GithubClient client = mock(GithubClient.class);
        when(client.testCommitsUrl("owner", "name"))
            .thenReturn(HttpStatus.valueOf(404));
        LinkUpdater updater = new GithubCommitsLinkUpdater(client);

        boolean supports = updater.supports(URI.create("https://github.com/owner/name/commits"));

        assertThat(supports).isFalse();
    }

    @Test
    void emptyMessageIsReturnedOnUnsupportedLink() {
        LinkUpdater updater = new GithubCommitsLinkUpdater(null);
        OffsetDateTime yesterday = OffsetDateTime.now().minusDays(1);
        URI url = URI.create("https://stackoverflow/questions/123/question-title");

        Optional<String> message = updater.tryUpdate(url, yesterday);

        assertThat(message).isEmpty();
    }

    @Test
    void emptyMessageIsReturnedOnNoUpdates() {
        OffsetDateTime yesterday = OffsetDateTime.now().minusDays(1);
        GithubClient client = mock(GithubClient.class);
        when(client.getCommitsSince("owner", "repo", yesterday.format(ISO8601_FORMATTER)))
            .thenReturn(new CommitsResponse(List.of()));
        LinkUpdater updater = new GithubCommitsLinkUpdater(client);
        URI url = URI.create("https://github.com/owner/repo/commits");

        Optional<String> message = updater.tryUpdate(url, yesterday);

        assertThat(message).isEmpty();
    }

    @Test
    void nonEmptyMessageIsReturnedOnUpdates() {
        OffsetDateTime yesterday = OffsetDateTime.now().minusDays(1);
        GithubClient client = mock(GithubClient.class);
        when(client.getCommitsSince("owner", "repo", yesterday.format(ISO8601_FORMATTER)))
            .thenReturn(new CommitsResponse(List.of(new CommitsResponse.CommitResponse("example.com"))));
        LinkUpdater updater = new GithubCommitsLinkUpdater(client);
        URI url = URI.create("https://github.com/owner/repo/commits");

        Optional<String> message = updater.tryUpdate(url, yesterday);

        assertThat(message).isNotEmpty();
    }
}
