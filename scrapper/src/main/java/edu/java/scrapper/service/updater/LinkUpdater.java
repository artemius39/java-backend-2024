package edu.java.scrapper.service.updater;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface LinkUpdater {
    // return update message if update occurred, otherwise return empty optional
    Optional<String> tryUpdate(URI url, OffsetDateTime lastUpdatedAt);

    boolean supports(URI url);
}
