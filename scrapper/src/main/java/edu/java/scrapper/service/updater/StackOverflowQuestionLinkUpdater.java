package edu.java.scrapper.service.updater;

import edu.java.scrapper.client.StackOverflowClient;
import edu.java.scrapper.dto.stackoverflow.QuestionResponse;
import edu.java.scrapper.dto.stackoverflow.QuestionsResponse;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StackOverflowQuestionLinkUpdater extends BaseUpdater<Long> {
    private static final Pattern STACKOVERFLOW_QUESTION_PATTERN =
        Pattern.compile("https://stackoverflow.com/questions/([^/]+)/[^/]+$");

    private final StackOverflowClient stackOverflowClient;

    @Override
    public Optional<String> tryUpdate(URI url, OffsetDateTime lastUpdatedAt) {
        Long questionId = parseUrl(url);
        if (questionId == null) {
            return Optional.empty();
        }

        QuestionsResponse response = stackOverflowClient.getLastModificationTime(questionId);
        QuestionResponse question = response.items().getFirst();
        if (question.lastActivityDate().isAfter(lastUpdatedAt)) {
            return Optional.of("В вопросе %s произошло обновление".formatted(url));
        }
        return Optional.empty();
    }

    @Override
    protected HttpStatus testUrl(Long parsedUrl) {
        return stackOverflowClient.testQuestionUrl(parsedUrl);
    }

    @Override
    protected Long parseUrl(URI url) {
        Matcher matcher = STACKOVERFLOW_QUESTION_PATTERN.matcher(url.toString());
        try {
            return matcher.find() ? Long.parseLong(matcher.group(1)) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
