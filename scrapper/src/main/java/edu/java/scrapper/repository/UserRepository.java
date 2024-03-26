package edu.java.scrapper.repository;

import edu.java.scrapper.model.User;

public interface UserRepository {
    User find(long id);

    User add(long id);

    void remove(long id);
}
