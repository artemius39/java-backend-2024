package edu.java.scrapper.controller;

import edu.java.scrapper.dto.bot.AddLinkRequest;
import edu.java.scrapper.dto.bot.LinkResponse;
import edu.java.scrapper.dto.bot.ListLinksResponse;
import edu.java.scrapper.dto.bot.RemoveLinkRequest;
import edu.java.scrapper.exception.InvalidParameterException;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@AllArgsConstructor
public class ScrapperController {
    private final LinkService linkService;
    private final UserService userService;

    @PostMapping("/tg-chat/{id}")
    public void registerChat(@PathVariable String id) {
        long chatId = parseChatId(id);
        userService.register(chatId);
        log.info("Registered chat no. {}", chatId);
    }

    @DeleteMapping("/tg-chat/{id}")
    public void deleteChat(@PathVariable String id) {
        long chatId = parseChatId(id);
        userService.unregister(chatId);
        log.info("Deleted chat no. {}", chatId);
    }

    @GetMapping("/links")
    public ListLinksResponse getAllLinks(@RequestParam(name = "Tg-Chat-Id") String id) {
        long chatId = parseChatId(id);
        ListLinksResponse response = linkService.listAll(chatId);
        log.info("Fetched all links for chat no. {}: {}", response, chatId);
        return response;
    }

    @PostMapping("/links")
    public LinkResponse addLink(
        @RequestParam(name = "Tg-Chat-Id") String id,
        @RequestBody AddLinkRequest link
    ) {
        long chatId = parseChatId(id);
        log.info("Added link {} to chat no. {}", link, id);
        return linkService.add(chatId, link.link());
    }

    @DeleteMapping("/links")
    public LinkResponse removeLink(
        @RequestParam(name = "Tg-Chat-Id") String id,
        @RequestBody RemoveLinkRequest link
    ) {
        long chatId = parseChatId(id);
        log.info("Removed link {} from chat no. {}", link, id);
        return linkService.remove(chatId, link.link());
    }

    private long parseChatId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new InvalidParameterException("Invalid chat id: " + id);
        }
    }
}
