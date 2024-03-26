package edu.java.scrapper.service;

import edu.java.scrapper.exception.ChatNotFoundException;
import edu.java.scrapper.exception.UserAlreadyRegisteredException;
import edu.java.scrapper.model.User;
import edu.java.scrapper.repository.LinkRepository;
import edu.java.scrapper.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final LinkRepository linkRepository;

    @Override
    public void register(long chatId) {
        User user = userRepository.find(chatId);
        if (user != null) {
            throw new UserAlreadyRegisteredException(chatId);
        }
        userRepository.add(chatId);
    }

    @Override
    @Transactional
    public void unregister(long chatId) {
        User user = userRepository.find(chatId);
        if (user == null) {
            throw new ChatNotFoundException(chatId);
        }
        linkRepository.removeAllByUserId(chatId);
        linkRepository.removeAllUntracked();
        userRepository.remove(chatId);
    }
}
