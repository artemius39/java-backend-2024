package edu.java.scrapper.service.updater;

import edu.java.scrapper.client.StackOverflowClient;
import edu.java.scrapper.dto.stackoverflow.QuestionResponse;
import edu.java.scrapper.dto.stackoverflow.QuestionsResponse;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StackOverflowQuestionLinkUpdaterTest {
    @Test
    void validQuestionsAreSupported() {
        StackOverflowClient client = mock(StackOverflowClient.class);
        when(client.testQuestionUrl(123))
            .thenReturn(HttpStatus.valueOf(200));
        LinkUpdater updater = new StackOverflowQuestionLinkUpdater(client);

        boolean supports = updater.supports(URI.create("https://stackoverflow.com/questions/123/question-title"));

        assertThat(supports).isTrue();
    }

    @Test
    void nonStackOverflowQuestionLinksAreNotSupported() {
        LinkUpdater updater = new StackOverflowQuestionLinkUpdater(mock(StackOverflowClient.class));

        boolean supports = updater.supports(URI.create("https://api.github.com/repos/torvalds/linux"));

        assertThat(supports).isFalse();
    }

    @Test
    void nonIntegerIdsAreNotSupported() {
        LinkUpdater updater = new StackOverflowQuestionLinkUpdater(mock(StackOverflowClient.class));

        boolean supports = updater.supports(URI.create("https://stackoverflow.com/aboba/question-title"));

        assertThat(supports).isFalse();
    }

    @Test
    void tooLongIdsAreNotSupported() {
        LinkUpdater updater = new StackOverflowQuestionLinkUpdater(mock(StackOverflowClient.class));
        URI url = URI.create("https://stackoverflow.com/1234567890123456789012345678901234567890/question-title");

        boolean supports = updater.supports(url);

        assertThat(supports).isFalse();
    }

    @Test
    void nonExistingQuestionsAreNotSupported() {
        StackOverflowClient client = mock(StackOverflowClient.class);
        when(client.testQuestionUrl(123))
            .thenReturn(HttpStatus.valueOf(404));
        LinkUpdater updater = new StackOverflowQuestionLinkUpdater(client);

        boolean supports = updater.supports(URI.create("https://stackoverflow.com/questions/123/question-title"));

        assertThat(supports).isFalse();
    }

    @Test
    void emptyMessageIsReturnedOnNoUpdate() {
        OffsetDateTime yesterday = OffsetDateTime.now().minusDays(1);
        URI url = URI.create("https://stackoverflow.com/questions/123/question-title");

        StackOverflowClient client = mock(StackOverflowClient.class);
        when(client.getLastModificationTime(123))
            .thenReturn(new QuestionsResponse(List.of(new QuestionResponse(yesterday))));
        LinkUpdater updater = new StackOverflowQuestionLinkUpdater(client);

        Optional<String> message = updater.tryUpdate(url, yesterday);

        assertThat(message).isEmpty();
    }

    @Test
    void emptyMessageIsReturnedOnUnsupportedLink() {
        LinkUpdater updater = new StackOverflowQuestionLinkUpdater(null);

        Optional<String> message = updater.tryUpdate(URI.create("https://github.com/owner/repo"), null);

        assertThat(message).isEmpty();
    }

    @Test
    void nonEmptyMessageIsReturnedOnUpdate() {
        OffsetDateTime today = OffsetDateTime.now();
        OffsetDateTime yesterday = today.minusDays(1);
        URI url = URI.create("https://stackoverflow.com/questions/123/question-title");

        StackOverflowClient client = mock(StackOverflowClient.class);
        when(client.getLastModificationTime(123))
            .thenReturn(new QuestionsResponse(List.of(new QuestionResponse(today))));
        LinkUpdater updater = new StackOverflowQuestionLinkUpdater(client);

        Optional<String> message = updater.tryUpdate(url, yesterday);

        assertThat(message).isNotEmpty();
    }
}
