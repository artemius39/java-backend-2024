package edu.java.scrapper.service;

import edu.java.scrapper.dto.bot.LinkResponse;
import edu.java.scrapper.dto.bot.ListLinksResponse;
import edu.java.scrapper.exception.ChatNotFoundException;
import edu.java.scrapper.exception.LinkAlreadyTrackedException;
import edu.java.scrapper.exception.LinkNotTrackedException;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.model.User;
import edu.java.scrapper.repository.LinkRepository;
import edu.java.scrapper.repository.UserRepository;
import java.net.URI;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
@AllArgsConstructor
public class LinkServiceImpl implements LinkService {
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;

    @Override
    public LinkResponse add(long chatId, URI url) {
        Link link = linkRepository.findByUrl(url);
        if (link == null) {
            link = linkRepository.add(url);
        }
        User user = userRepository.find(chatId);
        if (user == null) {
            throw new ChatNotFoundException(chatId);
        }
        int insertedRows = linkRepository.tryAdd(chatId, link.getId());
        if (insertedRows == 0) {
            throw new LinkAlreadyTrackedException(url, chatId);
        }
        return toDto(link);
    }

    @Override
    @Transactional
    public LinkResponse remove(long chatId, URI url) {
        Link link = linkRepository.findByUrl(url);
        if (link == null) {
            throw new LinkNotTrackedException(url);
        }
        User user = userRepository.find(chatId);
        if (user == null) {
            throw new ChatNotFoundException(chatId);
        }
        int deletedRows = linkRepository.tryRemove(chatId, link.getId());
        if (deletedRows == 0) {
            throw new LinkNotTrackedException(url, chatId);
        }
        long subscriberCount = linkRepository.countByLinkId(link.getId());
        if (subscriberCount == 0) {
            linkRepository.remove(link);
        }
        return toDto(link);
    }

    @Override
    public ListLinksResponse listAll(long chatId) {
        User user = userRepository.find(chatId);
        if (user == null) {
            throw new ChatNotFoundException(chatId);
        }
        return new ListLinksResponse(linkRepository.findByUserId(chatId)
            .stream()
            .map(this::toDto)
            .toList());
    }

    private LinkResponse toDto(Link link) {
        return new LinkResponse(link.getId(), link.getUrl());
    }
}
