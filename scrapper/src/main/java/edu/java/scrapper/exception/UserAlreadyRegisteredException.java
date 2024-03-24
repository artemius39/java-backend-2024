package edu.java.scrapper.exception;

public class UserAlreadyRegisteredException extends RuntimeException {
    public UserAlreadyRegisteredException(long id) {
        super("User with id " + id + " is already registered");
    }
}
