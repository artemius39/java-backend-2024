package edu.java.scrapper.exception;

import java.net.URI;

@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class LinkNotTrackedException extends RuntimeException {
    public LinkNotTrackedException(URI url, long chatId) {
        super("Ссылка " + url + " не отслеживается чатом " + chatId);
    }

    public LinkNotTrackedException(URI url) {
        super("Ссылка " + url + " не отслеживается");
    }
}
