package edu.java.scrapper.service;

import edu.java.scrapper.dto.bot.LinkResponse;
import edu.java.scrapper.dto.bot.ListLinksResponse;
import edu.java.scrapper.exception.ChatNotFoundException;
import edu.java.scrapper.exception.LinkAlreadyTrackedException;
import edu.java.scrapper.exception.LinkNotTrackedException;
import edu.java.scrapper.model.jpa.Link;
import edu.java.scrapper.model.jpa.User;
import edu.java.scrapper.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.repository.jpa.JpaUserRepository;
import java.net.URI;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
public class JpaLinkService implements LinkService {
    private final JpaLinkRepository linkRepository;
    private final JpaUserRepository userRepository;

    @Override
    @Transactional
    public LinkResponse add(long chatId, URI url) {
        User user = userRepository.findById(chatId)
            .orElseThrow(() -> new ChatNotFoundException(chatId));

        Link link = linkRepository.findByUrl(url.toString())
            .orElseGet(() -> {
                Link newLink = new Link();
                newLink.setUrl(url.toString());
                return linkRepository.save(newLink);
            });

        if (!user.getLinks().add(link)) {
            throw new LinkAlreadyTrackedException(url, chatId);
        }
        userRepository.save(user);
        return toDto(link);
    }

    @Override
    public LinkResponse remove(long chatId, URI url) {
        Link link = linkRepository.findByUrl(url.toString())
            .orElseThrow(() -> new LinkNotTrackedException(url));
        User user = userRepository.findById(chatId)
            .orElseThrow(() -> new ChatNotFoundException(chatId));
        boolean ok = user.getLinks().remove(link);
        if (!ok) {
            throw new LinkNotTrackedException(url, chatId);
        }
        userRepository.save(user);
        return toDto(link);
    }

    @Override
    public ListLinksResponse listAll(long chatId) {
        User user = userRepository.findById(chatId)
            .orElseThrow(() -> new ChatNotFoundException(chatId));
        return linkRepository.findAllByUsersContaining(user)
            .stream()
            .map(this::toDto)
            .collect(Collectors.collectingAndThen(Collectors.toList(), ListLinksResponse::new));
    }

    private LinkResponse toDto(Link link) {
        return new LinkResponse(link.getId(), URI.create(link.getUrl()));
    }
}
