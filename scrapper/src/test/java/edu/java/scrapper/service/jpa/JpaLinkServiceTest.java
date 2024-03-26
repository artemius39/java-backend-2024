package edu.java.scrapper.service.jpa;

import edu.java.scrapper.dto.bot.LinkResponse;
import edu.java.scrapper.dto.bot.ListLinksResponse;
import edu.java.scrapper.exception.ChatNotFoundException;
import edu.java.scrapper.exception.LinkAlreadyTrackedException;
import edu.java.scrapper.exception.LinkNotTrackedException;
import edu.java.scrapper.model.jpa.Link;
import edu.java.scrapper.model.jpa.User;
import edu.java.scrapper.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.repository.jpa.JpaUserRepository;
import edu.java.scrapper.service.JpaLinkService;
import edu.java.scrapper.service.LinkService;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaLinkServiceTest {
    @Test
    void exceptionIsThrownWhenAddingLinkToUnregisteredUser() {
        JpaLinkRepository linkRepository = mock(JpaLinkRepository.class);
        JpaUserRepository userRepository = mock(JpaUserRepository.class);
        when(userRepository.findById(1L))
            .thenReturn(Optional.empty());
        LinkService linkService = new JpaLinkService(linkRepository, userRepository);

        assertThrows(ChatNotFoundException.class, () -> linkService.add(1, URI.create("example.com")));
    }

    @Test
    void exceptionIsThrownWhenAddingLinkThatIsAlreadyTracked() {
        URI url = URI.create("example.com");
        Link link = new Link();
        User user = new User();
        user.setId(1L);
        user.getLinks().add(link);
        link.getUsers().add(user);

        JpaLinkRepository linkRepository = mock(JpaLinkRepository.class);
        JpaUserRepository userRepository = mock(JpaUserRepository.class);
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(user));
        when(linkRepository.findByUrl("example.com"))
            .thenReturn(Optional.of(link));
        LinkService linkService = new JpaLinkService(linkRepository, userRepository);

        assertThrows(LinkAlreadyTrackedException.class, () -> linkService.add(1, url));
    }

    @Test
    void newLinkIsSuccessfullyAdded() {
        Link link = new Link();
        link.setUrl("example.com");
        Link savedLink = new Link();
        savedLink.setId(1L);
        savedLink.setUrl("example.com");
        User user = new User();
        user.setId(1L);

        JpaUserRepository userRepository = mock(JpaUserRepository.class);
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(user));
        JpaLinkRepository linkRepository = mock(JpaLinkRepository.class);
        when(linkRepository.findByUrl("example.com"))
            .thenReturn(Optional.empty());
        when(linkRepository.save(link))
            .thenReturn(savedLink);
        LinkService linkService = new JpaLinkService(linkRepository, userRepository);

        LinkResponse response = linkService.add(1, URI.create("example.com"));

        verify(linkRepository).save(link);
        verify(userRepository).save(user);
        assertThat(user.getLinks()).containsExactly(savedLink);
        assertThat(response.id()).isOne();
        assertThat(response.url()).isEqualTo(URI.create("example.com"));
    }

    @Test
    void alreadySavedLinkIsSuccessfullyAdded() {
        Link link = new Link();
        link.setId(1L);
        link.setUrl("example.com");
        User user = new User();
        user.setId(1L);

        JpaUserRepository userRepository = mock(JpaUserRepository.class);
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(user));
        JpaLinkRepository linkRepository = mock(JpaLinkRepository.class);
        when(linkRepository.findByUrl("example.com"))
            .thenReturn(Optional.of(link));
        LinkService linkService = new JpaLinkService(linkRepository, userRepository);

        LinkResponse response = linkService.add(1, URI.create("example.com"));

        verify(userRepository).save(user);
        assertThat(user.getLinks()).containsExactly(link);
        assertThat(response.id()).isOne();
        assertThat(response.url()).isEqualTo(URI.create("example.com"));
    }

    @Test
    void exceptionIsThrownWhenUntrackingUnknownLink() {
        JpaLinkRepository linkRepository = mock(JpaLinkRepository.class);
        when(linkRepository.findByUrl("example.com"))
            .thenReturn(Optional.empty());
        LinkService linkService = new JpaLinkService(linkRepository, mock(JpaUserRepository.class));

        assertThrows(LinkNotTrackedException.class, () -> linkService.remove(1, URI.create("example.com")));
    }

    @Test
    void exceptionIsThrownWhenUntrackingLinkByAnUnregisteredUser() {
        JpaUserRepository userRepository = mock(JpaUserRepository.class);
        when(userRepository.findById(1L))
            .thenReturn(Optional.empty());
        JpaLinkRepository linkRepository = mock(JpaLinkRepository.class);
        when(linkRepository.findByUrl("example.com"))
            .thenReturn(Optional.of(mock(Link.class)));
        LinkService linkService = new JpaLinkService(linkRepository, userRepository);

        assertThrows(ChatNotFoundException.class, () -> linkService.add(1, URI.create("example.com")));
    }

    @Test
    void exceptionIsThrownWhenUntrackingAnUntrackedLink() {
        User user = new User();
        user.setId(1L);
        user.setLinks(new HashSet<>());
        Link link = new Link();

        JpaUserRepository userRepository = mock(JpaUserRepository.class);
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(user));
        JpaLinkRepository linkRepository = mock(JpaLinkRepository.class);
        when(linkRepository.findByUrl("example.com"))
            .thenReturn(Optional.of(link));
        LinkService linkService = new JpaLinkService(linkRepository, userRepository);

        assertThrows(LinkNotTrackedException.class, () -> linkService.remove(1, URI.create("example.com")));
    }

    @Test
    void linkIsRemovedSuccessfully() {
        Link link = new Link();
        link.setId(1L);
        link.setUrl("example.com");
        User user = new User();
        user.setId(1L);
        user.getLinks().add(link);
        link.getUsers().add(user);

        JpaUserRepository userRepository = mock(JpaUserRepository.class);
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(user));
        JpaLinkRepository linkRepository = mock(JpaLinkRepository.class);
        when(linkRepository.findByUrl("example.com"))
            .thenReturn(Optional.of(link));
        LinkService linkService = new JpaLinkService(linkRepository, userRepository);

        LinkResponse response = linkService.remove(1, URI.create("example.com"));

        assertThat(user.getLinks()).doesNotContain(link);
        verify(userRepository).save(user);
        assertThat(response.id()).isOne();
        assertThat(response.url()).isEqualTo(URI.create("example.com"));
    }

    @Test
    void exceptionIsThrownWhenListingLinksFromUnregisteredChat() {
        JpaUserRepository userRepository = mock(JpaUserRepository.class);
        when(userRepository.findById(1L))
            .thenReturn(Optional.empty());
        LinkService linkService = new JpaLinkService(mock(JpaLinkRepository.class), userRepository);

        assertThrows(ChatNotFoundException.class, () -> linkService.listAll(1));
    }

    @Test
    void linksAreListedSuccessfully() {
        Link link1 = new Link();
        link1.setId(1L);
        link1.setUrl("example.com");
        link1.setUpdatedAt(OffsetDateTime.now());
        Link link2 = new Link();
        link2.setId(2L);
        link2.setUrl("example.org");
        link2.setUpdatedAt(OffsetDateTime.now());

        User user = new User();
        user.setId(1L);
        user.getLinks().add(link1);
        user.getLinks().add(link2);

        JpaUserRepository userRepository = mock(JpaUserRepository.class);
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(user));
        JpaLinkRepository linkRepository = mock(JpaLinkRepository.class);
        when(linkRepository.findAllByUsersContaining(user))
            .thenReturn(List.of(link1, link2));
        LinkService linkService = new JpaLinkService(linkRepository, userRepository);

        ListLinksResponse response = linkService.listAll(1);

        assertThat(response.size()).isEqualTo(2);
        assertThat(response.links()).containsExactlyInAnyOrder(
            new LinkResponse(1, URI.create("example.com")),
            new LinkResponse(2, URI.create("example.org"))
        );
    }
}
