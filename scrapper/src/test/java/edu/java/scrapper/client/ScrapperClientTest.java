package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.scrapper.configuration.ClientConfiguration;
import edu.java.scrapper.dto.bot.LinkUpdateRequest;
import edu.java.scrapper.exception.ApiException;
import java.util.List;
import org.junit.jupiter.api.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.jsonResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.Assert.assertThrows;

@WireMockTest(httpPort = 8080)
public class ScrapperClientTest {
    @Test
    void errorsAreHandled() {
        ScrapperClient client = new ClientConfiguration().scrapperClient("http://localhost:8080");
        stubFor(post("/updates").willReturn(jsonResponse("""
                                {
                                    "description": "stub exception",
                                    "code": "400",
                                    "exceptionName": "StubException",
                                    "exceptionMessage": "stub exception",
                                    "stackTrace": []
                                }""", 400)));
        LinkUpdateRequest request = new LinkUpdateRequest(0, null, "", List.of());

        assertThrows(ApiException.class, () -> client.sendUpdate(request));
    }
}
