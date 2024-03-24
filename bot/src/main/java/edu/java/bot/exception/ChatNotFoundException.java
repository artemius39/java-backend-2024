package edu.java.bot.exception;

public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException(long id) {
        super("Chat no. " + id + " not found");
    }
}
