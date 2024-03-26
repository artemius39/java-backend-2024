package edu.java.scrapper.service.jdbc;

import edu.java.scrapper.exception.ChatNotFoundException;
import edu.java.scrapper.exception.UserAlreadyRegisteredException;
import edu.java.scrapper.model.User;
import edu.java.scrapper.repository.LinkRepository;
import edu.java.scrapper.repository.UserRepository;
import edu.java.scrapper.service.UserService;
import edu.java.scrapper.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {
    @Test
    void newUserIsRegisteredSuccessfully() {
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.find(1)).thenReturn(null);
        UserService userService = new UserServiceImpl(userRepository, null);

        userService.register(1);

        verify(userRepository).add(1);
    }

    @Test
    void exceptionIsThrownWhenRegisteringAlreadyRegisteredUser() {
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.find(1)).thenReturn(mock(User.class));
        UserService userService = new UserServiceImpl(userRepository, null);

        assertThrows(UserAlreadyRegisteredException.class, () -> userService.register(1));
    }

    @Test
    void exceptionIsThrownWhenUnregisteringUnregisteredUser() {
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.find(1)).thenReturn(null);
        UserService userService = new UserServiceImpl(userRepository, null);

        assertThrows(ChatNotFoundException.class, () -> userService.unregister(1));
    }

    @Test
    void removeTest() {
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.find(1)).thenReturn(new User(1L));
        LinkRepository linkRepository = mock(LinkRepository.class);
        UserService userService = new UserServiceImpl(userRepository, linkRepository);

        userService.unregister(1);

        verify(userRepository).remove(1);
        verify(linkRepository).removeAllByUserId(1);
        verify(linkRepository).removeAllUntracked();
    }
}
