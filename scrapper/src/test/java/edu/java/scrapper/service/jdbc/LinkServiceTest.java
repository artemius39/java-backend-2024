package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.dto.bot.LinkResponse;
import edu.java.scrapper.dto.bot.ListLinksResponse;
import edu.java.scrapper.exception.ChatNotFoundException;
import edu.java.scrapper.exception.LinkAlreadyTrackedException;
import edu.java.scrapper.exception.LinkNotTrackedException;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.model.User;
import edu.java.scrapper.repository.LinkRepository;
import edu.java.scrapper.repository.UserRepository;
import edu.java.scrapper.service.LinkService;
import edu.java.scrapper.service.LinkServiceImpl;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LinkServiceTest {
    @Test
    void exceptionIsThrownWhenAddingLinkToUnregisteredUser() {
        LinkRepository linkRepository = mock(LinkRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        LinkService linkService = new LinkServiceImpl(linkRepository, userRepository);

        assertThrows(ChatNotFoundException.class, () -> linkService.add(1, URI.create("example.com")));
    }

    @Test
    void exceptionIsThrownWhenAddingLinkThatIsAlreadyTracked() {
        URI url = URI.create("example.com");
        LinkRepository linkRepository = mock(LinkRepository.class);
        when(linkRepository.findByUrl(url))
            .thenReturn(new Link(1L, url));
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.find(1))
            .thenReturn(mock(User.class));
        when(linkRepository.tryAdd(1, 1))
            .thenReturn(0);
        LinkService linkService = new LinkServiceImpl(linkRepository, userRepository);

        assertThrows(LinkAlreadyTrackedException.class, () -> linkService.add(1, url));
    }

    @Test
    void newLinkIsTrackedSuccessfully() {
        URI url = URI.create("example.com");
        LinkRepository linkRepository = mock(LinkRepository.class);
        when(linkRepository.findByUrl(url))
            .thenReturn(new Link(1L, url));
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.find(1))
            .thenReturn(new User(1L));
        when(linkRepository.tryAdd(1, 1))
            .thenReturn(1);
        LinkService linkService = new LinkServiceImpl(linkRepository, userRepository);

        LinkResponse response = linkService.add(1, url);

        assertThat(response).isEqualTo(new LinkResponse(1L, url));
    }

    @Test
    void newLinkIsAddedToDatabase() {
        URI url = URI.create("example.com");
        LinkRepository linkRepository = mock(LinkRepository.class);
        when(linkRepository.findByUrl(url))
            .thenReturn(null);
        when(linkRepository.add(url))
            .thenReturn(new Link(1L, url));
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.find(1))
            .thenReturn(new User(1L));
        when(linkRepository.tryAdd(1, 1))
            .thenReturn(1);
        LinkService linkService = new LinkServiceImpl(linkRepository, userRepository);

        LinkResponse response = linkService.add(1, url);

        assertThat(response).isEqualTo(new LinkResponse(1L, url));
        verify(linkRepository).add(url);
    }

    @Test
    void exceptionIsThrownWhenRemovingLinkThatIsNotTrackedByAnyone() {
        URI url = URI.create("example.com");
        LinkRepository linkRepository = mock(LinkRepository.class);
        when(linkRepository.findByUrl(url))
            .thenReturn(null);
        UserRepository userRepository = mock(UserRepository.class);
        LinkService linkService = new LinkServiceImpl(linkRepository, userRepository);

        assertThrows(LinkNotTrackedException.class, () -> linkService.remove(1, url));
    }

    @Test
    void exceptionIsThrownWhenRemovingLinkFromUnregisteredUser() {
        URI url = URI.create("example.com");
        LinkRepository linkRepository = mock(LinkRepository.class);
        when(linkRepository.findByUrl(url))
            .thenReturn(new Link(1L, url));
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.find(1))
            .thenReturn(null);
        LinkService linkService = new LinkServiceImpl(linkRepository, userRepository);

        assertThrows(ChatNotFoundException.class, () -> linkService.remove(1, url));
    }

    @Test
    void exceptionIsThrownWhenRemovingLinkNotTrackedByUser() {
        URI url = URI.create("example.com");
        LinkRepository linkRepository = mock(LinkRepository.class);
        when(linkRepository.findByUrl(url))
            .thenReturn(new Link(1L, url));
        when(linkRepository.tryRemove(1, 1))
            .thenReturn(0);
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.find(1))
            .thenReturn(new User(1L));
        LinkService linkService = new LinkServiceImpl(linkRepository, userRepository);

        assertThrows(LinkNotTrackedException.class, () -> linkService.remove(1, url));
    }

    @Test
    void linkTrackedByTheUserIsUntrackedSuccessfully() {
        URI url = URI.create("example.com");
        LinkRepository linkRepository = mock(LinkRepository.class);
        Link link = new Link(1L, url);
        when(linkRepository.findByUrl(url))
            .thenReturn(link);
        when(linkRepository.tryRemove(1, 1))
            .thenReturn(1);
        when(linkRepository.countByLinkId(1))
            .thenReturn(10L);
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.find(1))
            .thenReturn(new User(1L));
        LinkService linkService = new LinkServiceImpl(linkRepository, userRepository);

        LinkResponse response = linkService.remove(1, url);

        assertThat(response).isEqualTo(new LinkResponse(1L, url));
        verify(linkRepository, never()).remove(link);
    }

    @Test
    void linkIsDeletedWhenLastUserUnsubscribes() {
        URI url = URI.create("example.com");
        LinkRepository linkRepository = mock(LinkRepository.class);
        Link link = new Link(1L, url);
        when(linkRepository.findByUrl(url))
            .thenReturn(link);
        when(linkRepository.tryRemove(1, 1))
            .thenReturn(1);
        when(linkRepository.countByLinkId(1))
            .thenReturn(0L);
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.find(1))
            .thenReturn(new User(1L));
        LinkService linkService = new LinkServiceImpl(linkRepository, userRepository);

        linkService.remove(1, url);

        verify(linkRepository).remove(link);
    }

    @Test
    void exceptionIsThrownWhenListingLinksFromUnregisteredUser() {
        LinkRepository linkRepository = mock(LinkRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.find(1))
            .thenReturn(null);
        LinkService linkService = new LinkServiceImpl(linkRepository, userRepository);

        assertThrows(ChatNotFoundException.class, () -> linkService.listAll(1));
    }

    @Test
    void linksAreListedSuccessfully() {
        LinkRepository linkRepository = mock(LinkRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.find(1)).thenReturn(new User(1L));
        URI url1 = URI.create("1");
        URI url2 = URI.create("2");
        when(linkRepository.findByUserId(1L))
            .thenReturn(List.of(
                new Link(1L, url1),
                new Link(2L, url2)
            ));
        LinkService linkService = new LinkServiceImpl(linkRepository, userRepository);

        ListLinksResponse response = linkService.listAll(1);

        assertThat(response.links()).containsExactlyInAnyOrder(
            new LinkResponse(1, url1),
            new LinkResponse(2, url2)
        );
        assertThat(response.size()).isEqualTo(2);
    }
}
