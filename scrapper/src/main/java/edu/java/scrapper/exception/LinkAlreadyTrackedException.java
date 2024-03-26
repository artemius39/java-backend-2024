package edu.java.scrapper.exception;

import java.net.URI;

@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class LinkAlreadyTrackedException extends RuntimeException {
    public LinkAlreadyTrackedException(URI url, long chatId) {
        super("Ссылка " + url + " уже отслеживается в чате " + chatId);
    }

    public LinkAlreadyTrackedException(String link) {
        super("Ссылка " + link + " уже отслеживается");
    }
}
