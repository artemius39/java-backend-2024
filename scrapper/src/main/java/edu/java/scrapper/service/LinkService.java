package edu.java.scrapper.service;

import edu.java.scrapper.dto.bot.LinkResponse;
import edu.java.scrapper.dto.bot.ListLinksResponse;
import java.net.URI;

public interface LinkService {
    LinkResponse add(long chatId, URI url);

    LinkResponse remove(long chatId, URI url);

    ListLinksResponse listAll(long chatId);
}
