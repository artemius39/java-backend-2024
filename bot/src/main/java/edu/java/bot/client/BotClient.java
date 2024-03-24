package edu.java.bot.client;

import edu.java.bot.dto.request.AddLinkRequest;
import edu.java.bot.dto.request.RemoveLinkRequest;
import edu.java.bot.dto.response.LinkResponse;
import edu.java.bot.dto.response.ListLinksResponse;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface BotClient {
    @PostExchange("/tg-chat/{id}")
    void registerChat(@PathVariable long id);

    @DeleteExchange("/tg-chat/{id}")
    void deleteChat(@PathVariable long id);

    @GetExchange("/links")
    ListLinksResponse listTrackedLinks(@RequestParam(name = "Tg-Chat-Id") long tgChatId);

    @PostExchange("/links")
    LinkResponse addLink(@RequestBody AddLinkRequest request);

    @DeleteMapping("/links")
    LinkResponse removeLink(@RequestBody RemoveLinkRequest request);
}
