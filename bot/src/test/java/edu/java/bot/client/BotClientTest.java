package edu.java.bot.client;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.bot.configuration.ClientConfiguration;
import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.exception.ApiException;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.jsonResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.Assert.assertThrows;

@WireMockTest(httpPort = 8080)
public class BotClientTest {
    @Test
    void errorsAreHandled() {
        BotClient client = new ClientConfiguration().botClient("http://localhost:8080");
        stubFor(post("/links").willReturn(jsonResponse("""
                                {
                                    "description": "stub exception",
                                    "code": "400",
                                    "exceptionName": "StubException",
                                    "exceptionMessage": "stub exception",
                                    "stackTrace": []
                                }""", 400)));
        AddLinkRequest request = new AddLinkRequest(null);

        assertThrows(ApiException.class, () -> client.addLink(request));
    }
}
