package edu.java.scrapper.repository;

import edu.java.scrapper.IntegrationEnvironment;
import edu.java.scrapper.model.Link;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
abstract class LinkRepositoryTest extends IntegrationEnvironment {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    protected JdbcClient jdbcClient;

    private void truncateTime(Link link) {
        link.setLastUpdated(link.getLastUpdated().truncatedTo(ChronoUnit.SECONDS));
    }

    protected abstract LinkRepository getRepository();

    @Test
    @Transactional
    @Rollback
    void addLinkTest() {
        LinkRepository linkRepository = getRepository();

        linkRepository.add(URI.create("example.com"));
        Long count = jdbcClient.sql("select count(*) from link where url='example.com'")
            .query(Long.class)
            .single();

        assertThat(count).isOne();
    }

    @Test
    @Transactional
    @Rollback
    void removeLinkTest() {
        LinkRepository linkRepository = getRepository();

        jdbcClient.sql("insert into link (id, url, last_updated) values (1, 'example.com', now())").update();
        linkRepository.remove(new Link(1L, null, null));
        Long count = jdbcClient.sql("select count(*) from link where id=1")
            .query(Long.class)
            .single();

        assertThat(count).isZero();
    }

    @Test
    @Transactional
    @Rollback
    void addUserLinkTest() {
        LinkRepository linkRepository = getRepository();
        jdbcClient.sql("insert into link (id, url, last_updated) values (1, '', now())").update();
        jdbcClient.sql("insert into \"user\" (id, created_at) values (1, now())").update();

        int rows = linkRepository.tryAdd(1, 1);
        Long count = jdbcClient.sql("select count(*) from user_link where link_id=1 and link_id=1")
            .query(Long.class)
            .single();

        assertThat(count).isOne();
        assertThat(rows).isOne();
    }

    @Test
    @Transactional
    @Rollback
    void removeUserLinkTest() {
        LinkRepository linkRepository = getRepository();
        jdbcClient.sql("insert into link (id, url, last_updated) values (1, '', now())").update();
        jdbcClient.sql("insert into \"user\" (id, created_at) values (1, now())").update();
        jdbcClient.sql("insert into user_link (user_id, link_id) values (1, 1)");

        linkRepository.tryRemove(1, 1);
        Long count = jdbcClient.sql("select count(*) from user_link where link_id=1 and user_id=1")
            .query(Long.class)
            .single();

        assertThat(count).isZero();
    }

    @Test
    @Transactional
    @Rollback
    void countByLinkIdTest() {
        LinkRepository linkRepository = getRepository();
        jdbcClient.sql("insert into link (id, url, last_updated) values (1, '', now())").update();
        jdbcClient.sql("insert into \"user\" (id, created_at) values (1, now()), (2, now()), (3, now())").update();
        jdbcClient.sql("insert into user_link (user_id, link_id) values (1, 1), (2, 1), (3, 1)").update();

        long count = linkRepository.countByLinkId(1);

        assertThat(count).isEqualTo(3);
    }

    @Test
    @Transactional
    @Rollback
    void findByUrlTest() {
        LinkRepository linkRepository = getRepository();
        OffsetDateTime time = OffsetDateTime.now();
        jdbcClient.sql("insert into link (id, url, last_updated) values (1, 'example.com', ?)")
            .param(time)
            .update();
        URI url = URI.create("example.com");
        Link expected = new Link(1L, url, time);
        truncateTime(expected);

        Link actual = linkRepository.findByUrl(url);
        truncateTime(actual);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void updateTest() {
        LinkRepository linkRepository = getRepository();
        OffsetDateTime time = OffsetDateTime.now();
        jdbcClient.sql("insert into link (id, url, last_updated) values (1, 'example.com', ?)")
            .param(time)
            .update();
        URI url = URI.create("example.com");
        Link link = new Link(1L, url, time);
        truncateTime(link);
        OffsetDateTime newTime = time.plusDays(1);

        linkRepository.update(link, newTime);
        OffsetDateTime updatedTime = jdbcClient.sql("select link.last_updated from link where id=1")
            .query(OffsetDateTime.class)
            .single();

        assertThat(updatedTime.truncatedTo(ChronoUnit.SECONDS)).isEqualTo(newTime.truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    @Transactional
    @Rollback
    void findByUserIdTest() {
        LinkRepository linkRepository = getRepository();
        OffsetDateTime time = OffsetDateTime.now();

        jdbcTemplate.update("insert into \"user\" (id, created_at) values (1, now())");
        jdbcTemplate.update("insert into \"user\" (id, created_at) values (2, now())");
        jdbcTemplate.update(
            "insert into link (id, url, last_updated) values "
            + "(1, 'example.com', ?),"
            + "(2, 'example.org', ?),"
            + "(3, 'example.example', ?)",
            time, time, time
        );
        jdbcTemplate.update("insert into user_link (user_id, link_id) values (1, 1), (1, 2), (2, 3)");

        Collection<Link> links = linkRepository.findByUserId(1L);
        links.forEach(this::truncateTime);

        assertThat(links).containsExactly(
            new Link(1L, URI.create("example.com"), time.truncatedTo(ChronoUnit.SECONDS)),
            new Link(2L, URI.create("example.org"), time.truncatedTo(ChronoUnit.SECONDS))
        );
    }

    @Test
    @Transactional
    @Rollback
    void findByLastUpdateTimeTest() {
        LinkRepository linkRepository = getRepository();
        OffsetDateTime tooRecently = OffsetDateTime.now();
        OffsetDateTime oldEnough = OffsetDateTime.now().minusYears(1);
        jdbcTemplate.update(
            "insert into link (id, url, last_updated) values(1, 'example.com', ?), (2, 'example.org', ?)",
            tooRecently, oldEnough
        );

        Collection<Link> links = linkRepository.findByLastUpdateTime(Duration.of(1, ChronoUnit.DAYS));
        // truncate because db loses precision
        links.forEach(this::truncateTime);

        assertThat(links).containsExactly(new Link(
            2L, URI.create("example.org"),
            oldEnough.truncatedTo(ChronoUnit.SECONDS)
        ));
    }

    @Test
    @Transactional
    @Rollback
    void findByLinkIdTest() {
        LinkRepository linkRepository = getRepository();
        jdbcTemplate.update("insert into \"user\" (id, created_at) values(1, now()), (2, now()), (3, now())");
        jdbcTemplate.update("insert into link (id, url, last_updated) values (1, 'example.com', now())");
        jdbcTemplate.update("insert into user_link (user_id, link_id) values (1, 1), (2, 1)");

        List<Long> ids = linkRepository.findUserIdsByLinkId(1L);

        assertThat(ids).containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @Transactional
    @Rollback
    void removeAllByUserIdTest() {
        LinkRepository linkRepository = getRepository();
        jdbcClient.sql("insert into link (id, url, last_updated) values "
                       + "(1, '1', now()), (2, '2', now()), (3, '3', now())")
            .update();
        jdbcClient.sql("insert into \"user\" (id, created_at) values (1, now()), (2, now())").update();
        jdbcClient.sql("insert into user_link (user_id, link_id) values (1, 1), (1, 2), (1, 3), (2, 3)").update();

        linkRepository.removeAllByUserId(1);
        int count1 = jdbcClient.sql("select count(*) from user_link where user_id=1")
            .query(int.class)
            .single();
        int count2 = jdbcClient.sql("select count(*) from user_link where user_id=2")
            .query(int.class)
            .single();

        assertThat(count1).isZero();
        assertThat(count2).isOne();
    }

    @Test
    @Transactional
    @Rollback
    void removeAllUntrackedTest() {
        LinkRepository linkRepository = getRepository();
        jdbcClient.sql("insert into link (id, url, last_updated) values "
                       + "(1, '1', now()), (2, '2', now())")
            .update();
        jdbcClient.sql("insert into \"user\" (id, created_at) values (1, now())").update();
        jdbcClient.sql("insert into user_link (user_id, link_id) values (1, 1)").update();

        linkRepository.removeAllUntracked();
        List<Integer> ids = jdbcClient.sql("select id from link")
            .query(Integer.class)
            .list();

        assertThat(ids).containsExactlyInAnyOrder(1);
    }

    @Test
    @Transactional
    @Rollback
    void zeroIsReturnedWhenLinkIsAlreadyTracked() {
        LinkRepository linkRepository = getRepository();
        jdbcClient.sql("insert into link (id, url, last_updated) values "
                       + "(1, '1', now())")
            .update();
        jdbcClient.sql("insert into \"user\" (id, created_at) values (1, now())").update();
        jdbcClient.sql("insert into user_link (user_id, link_id) values (1, 1)").update();

        int rows = linkRepository.tryAdd(1, 1);

        assertThat(rows).isZero();
    }
}
