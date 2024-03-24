package edu.java.scrapper.repository;

import edu.java.scrapper.model.Link;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

public interface LinkRepository {
    Collection<Link> findByUserId(Long userId);

    Collection<Link> findByLastUpdateTime(Duration timeSinceLastUpdate);

    List<Long> findUserIdsByLinkId(Long id);

    int tryAdd(long userId, long linkId);

    int tryRemove(long userId, long linkId);

    Link add(URI url);

    void remove(Link link);

    Long countByLinkId(long linkId);

    Link findByUrl(URI url);

    void update(Link link, OffsetDateTime updateTime);

    void removeAllByUserId(long userId);

    void removeAllUntracked();
}
