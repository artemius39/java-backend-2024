package edu.java.scrapper.repository.jooq;

import edu.java.scrapper.model.Link;
import edu.java.scrapper.repository.LinkRepository;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import static edu.java.scrapper.model.jooq.Tables.LINK;
import static edu.java.scrapper.model.jooq.Tables.USER;
import static edu.java.scrapper.model.jooq.Tables.USER_LINK;

@Repository
@Primary
@AllArgsConstructor
public class JooqLinkRepository implements LinkRepository {
    private final DSLContext dslContext;

    @Override
    public Collection<Link> findByUserId(Long userId) {
        return dslContext.select()
            .from(LINK)
            .join(USER_LINK)
            .on(LINK.ID.eq(USER_LINK.LINK_ID))
            .where(USER_LINK.USER_ID.eq(userId))
            .fetchInto(Link.class);
    }

    @Override
    public Collection<Link> findByLastUpdateTime(Duration timeSinceLastUpdate) {
        return dslContext.select()
            .from(LINK)
            .where(LINK.LAST_UPDATED.lt(OffsetDateTime.now().minus(timeSinceLastUpdate)))
            .fetchInto(Link.class);
    }

    @Override
    public List<Long> findUserIdsByLinkId(Long id) {
        return dslContext.select(USER.ID)
            .from(USER)
            .join(USER_LINK)
            .on(USER.ID.eq(USER_LINK.USER_ID))
            .where(USER_LINK.LINK_ID.eq(id))
            .fetchInto(Long.class);
    }

    @Override
    public int tryAdd(long userId, long linkId) {
        return dslContext.insertInto(USER_LINK)
            .set(USER_LINK.USER_ID, userId)
            .set(USER_LINK.LINK_ID, linkId)
            .onConflictDoNothing()
            .execute();
    }

    @Override
    public int tryRemove(long userId, long linkId) {
        return dslContext.deleteFrom(USER_LINK)
            .where(USER_LINK.LINK_ID.eq(linkId).and(USER_LINK.USER_ID.eq(userId)))
            .execute();
    }

    @Override
    public Link add(URI url) {
        OffsetDateTime now = OffsetDateTime.now();
        Long id = dslContext.insertInto(LINK)
            .set(LINK.URL, url.toString())
            .set(LINK.LAST_UPDATED, now)
            .returningResult(LINK.ID)
            .fetchAnyInto(Long.class);
        return new Link(id, url, now);
    }

    @Override
    public void remove(Link link) {
        dslContext.deleteFrom(LINK)
            .where(LINK.ID.eq(link.getId()))
            .execute();
    }

    @Override
    public Long countByLinkId(long linkId) {
        return dslContext.selectCount()
            .from(USER_LINK)
            .where(USER_LINK.LINK_ID.eq(linkId))
            .fetchAnyInto(Long.class);
    }

    @Override
    public Link findByUrl(URI url) {
        return dslContext.select()
            .from(LINK)
            .where(LINK.URL.eq(url.toString()))
            .fetchAnyInto(Link.class);
    }

    @Override
    public void update(Link link, OffsetDateTime updateTime) {
        dslContext.update(LINK)
            .set(LINK.LAST_UPDATED, updateTime)
            .where(LINK.ID.eq(link.getId()))
            .execute();
    }

    @Override
    public void removeAllByUserId(long userId) {
        dslContext.deleteFrom(USER_LINK)
            .where(USER_LINK.USER_ID.eq(userId))
            .execute();
    }

    @Override
    public void removeAllUntracked() {
        dslContext.deleteFrom(LINK)
            .where(LINK.ID.notIn(
                dslContext.selectDistinct(USER_LINK.LINK_ID)
                    .from(USER_LINK)
                    .where(USER_LINK.LINK_ID.eq(LINK.ID))
            ))
            .execute();
    }
}
