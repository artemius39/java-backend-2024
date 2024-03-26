package edu.java.scrapper.repository.jpa;

import edu.java.scrapper.model.jpa.Link;
import edu.java.scrapper.model.jpa.User;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findByUrl(String url);

    List<Link> findAllByUsersContaining(User user);

    void deleteLinksByUsersEmpty();

    Collection<Link> findLinksByUpdatedAtBefore(OffsetDateTime time);
}
