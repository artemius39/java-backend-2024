package edu.java.scrapper.repository;

import edu.java.scrapper.IntegrationEnvironment;
import edu.java.scrapper.model.jpa.Link;
import edu.java.scrapper.model.jpa.User;
import edu.java.scrapper.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.repository.jpa.JpaUserRepository;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JpaLinkRepositoryTest extends IntegrationEnvironment {
    @Autowired
    private JdbcClient jdbcClient;
    @Autowired
    private JpaLinkRepository linkRepository;
    @Autowired
    private JpaUserRepository userRepository;

    @Test
    @Transactional
    @Rollback
    void findByUrlTest() {
        OffsetDateTime time = OffsetDateTime.now();
        jdbcClient.sql("insert into link (id, url, last_updated) values (1, 'a.com', ?)")
            .param(time)
            .update();
        Link expected = new Link();
        expected.setUrl("a.com");
        expected.setId(1L);
        expected.setUpdatedAt(time);

        Optional<Link> actual = linkRepository.findByUrl("a.com");

        assertThat(actual).isNotEmpty();
        assertThat(actual.get()).isEqualTo(expected);
    }

    @Test
    @Transactional
    @Rollback
    void findAllByUsersContainingTest() {
        jdbcClient.sql("insert into link (id, url, last_updated) values (1, '1', now()), (2, '2', now())")
            .update();
        jdbcClient.sql("insert into \"user\" (id, created_at) values (1, now())")
            .update();
        jdbcClient.sql("insert into user_link (user_id, link_id) values (1, 1)")
            .update();
        User user = userRepository.findById(1L).orElseThrow();
        Link link = linkRepository.findById(1L).orElseThrow();

        List<Link> links = linkRepository.findAllByUsersContaining(user);

        assertThat(links).containsExactly(link);
    }

    @Test
    @Transactional
    @Rollback
    void deleteLinksByUsersEmptyTest() {
        jdbcClient.sql("insert into link (id, url, last_updated) values (1, '1', now())")
            .update();

        linkRepository.deleteLinksByUsersEmpty();
        linkRepository.flush();

        int linkCount = jdbcClient.sql("select count(*) from link")
            .query(int.class)
            .single();

        assertThat(linkCount).isZero();
    }

    @Test
    @Transactional
    @Rollback
    void findLinksByUpdatedAtBeforeTest() {
        OffsetDateTime oldEnough = OffsetDateTime.now().minusYears(1);
        OffsetDateTime notOldEnough = OffsetDateTime.now();
        jdbcClient.sql("insert into link (id, url, last_updated) values (1, '1', ?), (2, '2', ?)")
            .param(oldEnough)
            .param(notOldEnough)
            .update();
        Link oldEnoughLink = linkRepository.findById(1L).orElseThrow();

        Collection<Link> links = linkRepository.findLinksByUpdatedAtBefore(OffsetDateTime.now().minusDays(1));

        assertThat(links).containsExactly(oldEnoughLink);
    }
}
