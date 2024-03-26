package edu.java.scrapper.service.updater;

import edu.java.scrapper.client.StackOverflowClient;
import edu.java.scrapper.dto.stackoverflow.AnswerResponse;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StackOverflowAnswerLinkUpdater extends BaseUpdater<Long> {
    private static final Pattern STACK_OVERFLOW_ANSWERS_PATTERN =
        Pattern.compile("https://stackoverflow.com/questions/(\\d+)/[^/]+/answers$");

    private final StackOverflowClient stackOverflowClient;

    @Override
    protected Long parseUrl(URI url) {
        Matcher matcher = STACK_OVERFLOW_ANSWERS_PATTERN.matcher(url.toString());
        return matcher.find() ? Long.valueOf(matcher.group(1)) : null;
    }

    @Override
    protected HttpStatus testUrl(Long questionId) {
        return stackOverflowClient.testAnswersUrl(questionId);
    }

    @Override
    @SuppressWarnings("MultipleStringLiterals")
    public Optional<String> tryUpdate(URI url, OffsetDateTime lastUpdatedAt) {
        Long questionId = parseUrl(url);
        if (questionId == null) {
            return Optional.empty();
        }

        AnswerResponse response = stackOverflowClient.getAnswersSince(questionId, lastUpdatedAt.toEpochSecond());
        List<AnswerResponse.Item> items = response.items();
        if (items.isEmpty()) {
            return Optional.empty();
        }
        String prefix;
        if (items.size() == 1) {
            prefix = "На вопрос " + url + " появился ответ: ";
        } else {
            prefix = "На вопрос " + url + " появились ответы: ";
        }
        return items.stream()
            .map(AnswerResponse.Item::answerId)
            .map(Object::toString)
            .collect(Collectors.collectingAndThen(
                Collectors.joining(", ", prefix, ""),
                Optional::of
            ));
    }
}
