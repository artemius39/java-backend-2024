package edu.java.scrapper.service.updater;

import edu.java.scrapper.client.StackOverflowClient;
import edu.java.scrapper.dto.stackoverflow.AnswerResponse;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StackOverflowAnswerLinkUpdaterTest {
    @Test
    void validUrlsAreSupported() {
        StackOverflowClient client = mock(StackOverflowClient.class);
        when(client.testAnswersUrl(123))
            .thenReturn(HttpStatus.valueOf(200));
        LinkUpdater linkUpdater = new StackOverflowAnswerLinkUpdater(client);
        URI url = URI.create("https://stackoverflow.com/questions/123/question-title/answers");

        boolean supports = linkUpdater.supports(url);

        assertThat(supports).isTrue();
    }

    @Test
    void invalidUrlsAreNotSupported() {
        LinkUpdater updater = new StackOverflowAnswerLinkUpdater(null);

        boolean supports = updater.supports(URI.create("example.com"));

        assertThat(supports).isFalse();
    }

    @Test
    void nonIntegerIdsAreNotSupported() {
        LinkUpdater updater = new StackOverflowAnswerLinkUpdater(null);
        URI url = URI.create("https://stackoverflow.com/questions/aboba/question-title/answers");

        boolean supports = updater.supports(url);

        assertThat(supports).isFalse();
    }

    @Test
    void tooLongIdsAreNotSupported() {
        LinkUpdater updater = new StackOverflowAnswerLinkUpdater(mock(StackOverflowClient.class));
        URI url = URI.create("https://stackoverflow.com/12345678901234567890123456789012345670/question-title/answers");

        boolean supports = updater.supports(url);

        assertThat(supports).isFalse();
    }

    @Test
    void nonExistingQuestionsAreNotSupported() {
        StackOverflowClient client = mock(StackOverflowClient.class);
        when(client.testAnswersUrl(123))
            .thenReturn(HttpStatus.valueOf(404));
        LinkUpdater updater = new StackOverflowAnswerLinkUpdater(client);
        URI url = URI.create("https://stackoverflow.com/questions/123/question-title/answers");

        boolean supports = updater.supports(url);

        assertThat(supports).isFalse();
    }

    @Test
    void emptyMessageIsReturnedOnUnsupportedLink() {
        LinkUpdater updater = new StackOverflowAnswerLinkUpdater(null);

        Optional<String> message = updater.tryUpdate(URI.create("https://github.com/owner/repo"), null);

        assertThat(message).isEmpty();
    }

    @Test
    void emptyMessageIsReturnedOnNoUpdate() {
        OffsetDateTime yesterday = OffsetDateTime.now().minusDays(1);
        URI url = URI.create("https://stackoverflow.com/questions/123/question-title/answers");

        StackOverflowClient client = mock(StackOverflowClient.class);
        when(client.getAnswersSince(123, yesterday.toEpochSecond()))
            .thenReturn(new AnswerResponse(List.of()));
        LinkUpdater updater = new StackOverflowAnswerLinkUpdater(client);

        Optional<String> message = updater.tryUpdate(url, yesterday);

        assertThat(message).isEmpty();
    }

    @Test
    void nonEmptyMessageIsReturnedOnUpdate() {
        OffsetDateTime yesterday = OffsetDateTime.now().minusDays(1);
        URI url = URI.create("https://stackoverflow.com/questions/123/question-title/answers");

        StackOverflowClient client = mock(StackOverflowClient.class);
        when(client.getAnswersSince(123, yesterday.toEpochSecond()))
            .thenReturn(new AnswerResponse(List.of(new AnswerResponse.Item(1234))));
        LinkUpdater updater = new StackOverflowAnswerLinkUpdater(client);

        Optional<String> message = updater.tryUpdate(url, yesterday);

        assertThat(message).isNotEmpty();
    }
}
