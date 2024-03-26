package edu.java.scrapper.service;

import edu.java.scrapper.exception.ChatNotFoundException;
import edu.java.scrapper.exception.UserAlreadyRegisteredException;
import edu.java.scrapper.model.jpa.User;
import edu.java.scrapper.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.repository.jpa.JpaUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
public class JpaUserService implements UserService {
    private final JpaUserRepository userRepository;
    private final JpaLinkRepository linkRepository;

    @Override
    public void register(long chatId) {
        if (userRepository.existsById(chatId)) {
            throw new UserAlreadyRegisteredException(chatId);
        }
        User user = new User();
        user.setId(chatId);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void unregister(long chatId) {
        User user = userRepository.findById(chatId)
            .orElseThrow(() -> new ChatNotFoundException(chatId));
        userRepository.delete(user);
        linkRepository.deleteLinksByUsersEmpty();
    }
}
