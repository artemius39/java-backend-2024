package edu.java.scrapper.repository;

import edu.java.scrapper.IntegrationEnvironment;
import edu.java.scrapper.model.jpa.Link;
import edu.java.scrapper.model.jpa.User;
import edu.java.scrapper.repository.jpa.JpaUserRepository;
import java.time.OffsetDateTime;
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
public class JpaUserRepositoryTest extends IntegrationEnvironment {
    @Autowired
    JpaUserRepository userRepository;
    @Autowired
    JdbcClient jdbcClient;

    @Test
    @Transactional
    @Rollback
    void findUserTest() {
        OffsetDateTime time = OffsetDateTime.now();
        jdbcClient.sql("insert into \"user\" (id, created_at) values (1, now())")
            .update();
        jdbcClient.sql("insert into link (id, url, last_updated) values (1, 'a.com', ?)")
            .param(time)
            .update();
        jdbcClient.sql("insert into user_link (user_id, link_id) values (1, 1)")
            .update();
        Link link = new Link();
        link.setId(1L);
        link.setUpdatedAt(time);
        link.setUrl("a.com");

        Optional<User> optionalUser = userRepository.findById(1L);
        assertThat(optionalUser).isNotEmpty();
        User user = optionalUser.get();

        assertThat(user.getLinks()).containsExactly(link);
    }

    @Test
    @Transactional
    @Rollback
    void addUserTest() {
        User user = new User();
        user.setId(1L);

        userRepository.save(user);
        userRepository.flush();
        int userCount = jdbcClient.sql("select count(*) from \"user\" where id=1")
            .query(int.class)
            .single();

        assertThat(userCount).isOne();
    }

    @Test
    @Transactional
    @Rollback
    void removeTest() {
        jdbcClient.sql("insert into \"user\" (id, created_at) values (1, now())")
            .update();
        jdbcClient.sql("insert into link (id, url, last_updated) values (1, 'a.com', now())")
            .update();
        jdbcClient.sql("insert into user_link (user_id, link_id) values (1, 1)")
            .update();

        userRepository.deleteById(1L);
        userRepository.flush();

        int userCount = jdbcClient.sql("select count(*) from \"user\" where id=1")
            .query(int.class)
            .single();
        int linkCount = jdbcClient.sql("select count(*) from link where id=1")
            .query(int.class)
            .single();

        assertThat(userCount).isZero();
        assertThat(linkCount).isOne();
    }

    @Test
    @Transactional
    @Rollback
    void findUsersByLinkTest() {
        jdbcClient.sql("insert into \"user\" (id, created_at) values (1, now()), (2, now())")
            .update();
        jdbcClient.sql("insert into link (id, url, last_updated) values (1, 'a.com', now())")
            .update();
        jdbcClient.sql("insert into user_link (user_id, link_id) values (1, 1)")
            .update();

        List<Long> ids = userRepository.findUsersByLink(1L);
        assertThat(ids).containsExactlyInAnyOrder(1L);
    }
}
