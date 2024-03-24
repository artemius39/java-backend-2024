package edu.java.scrapper.service.updater;

import java.net.URI;
import org.springframework.http.HttpStatus;

public abstract class BaseUpdater<T> implements LinkUpdater {
    @Override
    public boolean supports(URI url) {
        T parsingResult = parseUrl(url);
        if (parsingResult == null) {
            return false;
        }
        return testUrl(parsingResult).is2xxSuccessful();
    }

    protected abstract T parseUrl(URI url);

    protected abstract HttpStatus testUrl(T parsedUrl);
}
