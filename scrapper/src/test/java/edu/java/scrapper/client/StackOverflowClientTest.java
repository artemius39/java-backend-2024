package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.configuration.ClientConfiguration;
import edu.java.scrapper.dto.stackoverflow.QuestionResponse;
import edu.java.scrapper.dto.stackoverflow.QuestionsResponse;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest(httpPort = 8080)
class StackOverflowClientTest {
    ClientConfiguration configuration = new ClientConfiguration();

    @Test
    void timeIsParsedCorrectly() {
        StackOverflowClient client = configuration.stackOverflowClient("http://localhost:8080");
        stubFor(get("/2.3/questions/123?order=desc&sort=activity&site=stackoverflow").willReturn(okJson(
            """
                {
                    "items": [
                        {
                             "last_activity_date": 1594829479
                        }
                    ]
                }
                 """
        )));
        OffsetDateTime expected = OffsetDateTime.ofInstant(Instant.ofEpochSecond(1594829479), ZoneOffset.UTC);

        QuestionsResponse response = client.getLastModificationTime(123);

        assertThat(response.items()).singleElement().isEqualTo(new QuestionResponse(expected));
    }

    @Test
    void unknownFieldsAreIgnored() {
        StackOverflowClient client = configuration.stackOverflowClient("http://localhost:8080");
        stubFor(get("/2.3/questions/123?order=desc&sort=activity&site=stackoverflow").willReturn(okJson(
            """
                {
                    "items": [
                        {
                            "score": 12,
                            "last_activity_date": 1594829479,
                            "question_id": 123
                        }
                    ]
                }
                 """
        )));
        OffsetDateTime expected = OffsetDateTime.ofInstant(Instant.ofEpochSecond(1594829479), ZoneOffset.UTC);

        QuestionsResponse response = client.getLastModificationTime(123);

        assertThat(response.items()).singleElement().isEqualTo(new QuestionResponse(expected));
    }
}
