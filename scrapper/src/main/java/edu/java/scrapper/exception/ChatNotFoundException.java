package edu.java.scrapper.exception;

public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException(long id) {
        super("Chat no. " + id + " does not exist");
    }
}
