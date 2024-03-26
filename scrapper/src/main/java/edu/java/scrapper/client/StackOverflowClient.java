package edu.java.scrapper.client;

import edu.java.scrapper.dto.stackoverflow.AnswerResponse;
import edu.java.scrapper.dto.stackoverflow.QuestionsResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface StackOverflowClient {
    @GetExchange("/2.3/questions/{questionId}?order=desc&sort=activity&site=stackoverflow")
    QuestionsResponse getLastModificationTime(@PathVariable long questionId);

    @GetExchange("/2.3/questions/{questionId}?order=desc&sort=activity&site=stackoverflow")
    HttpStatus testQuestionUrl(@PathVariable long questionId);

    @GetExchange("/2.3/questions/{questionId}/answers?order=asc&sort=creation&site=stackoverflow")
    AnswerResponse getAnswersSince(
        @PathVariable long questionId,
        @RequestParam(name = "fromdate") long time
    );

    @GetExchange("/2.3/questions/{questionId}/answers?order=asc&sort=creation&site=stackoverflow")
    HttpStatus testAnswersUrl(@PathVariable long questionId);
}
