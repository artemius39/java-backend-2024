package edu.java.scrapper.service.jpa;

import edu.java.scrapper.exception.ChatNotFoundException;
import edu.java.scrapper.exception.UserAlreadyRegisteredException;
import edu.java.scrapper.model.jpa.User;
import edu.java.scrapper.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.repository.jpa.JpaUserRepository;
import edu.java.scrapper.service.JpaUserService;
import edu.java.scrapper.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JpaUserServiceTest {
    @Test
    void exceptionIsThrownWhenRegisteringAnAlreadyRegisteredUser() {
        JpaUserRepository userRepository = mock(JpaUserRepository.class);
        when(userRepository.existsById(1L))
            .thenReturn(true);
        UserService userService = new JpaUserService(userRepository, mock(JpaLinkRepository.class));

        assertThrows(UserAlreadyRegisteredException.class, () -> userService.register(1));
    }

    @Test
    void userIsSuccessfullyRegistered() {
        JpaUserRepository userRepository = mock(JpaUserRepository.class);
        when(userRepository.existsById(1L))
            .thenReturn(false);
        User user = new User();
        user.setId(1L);
        UserService userService = new JpaUserService(userRepository, mock(JpaLinkRepository.class));

        userService.register(1);

        verify(userRepository).save(user);
    }

    @Test
    void exceptionIsThrownWhenUnregisteringUnregisteredUser() {
        JpaUserRepository userRepository = mock(JpaUserRepository.class);
        when(userRepository.findById(1L))
            .thenReturn(Optional.empty());
        UserService userService = new JpaUserService(userRepository, mock(JpaLinkRepository.class));

        assertThrows(ChatNotFoundException.class, () -> userService.unregister(1));
    }

    @Test
    void userSuccessfullyUnregistered() {
        User user = new User();
        user.setId(1L);
        JpaUserRepository userRepository = mock(JpaUserRepository.class);
        when(userRepository.findById(1L))
            .thenReturn(Optional.of(user));
        JpaLinkRepository linkRepository = mock(JpaLinkRepository.class);
        UserService userService = new JpaUserService(userRepository, linkRepository);

        userService.unregister(1);

        verify(userRepository).delete(user);
        verify(linkRepository).deleteLinksByUsersEmpty();
    }
}
