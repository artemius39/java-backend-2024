package edu.java.scrapper.repository;

import edu.java.scrapper.IntegrationEnvironment;
import edu.java.scrapper.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
abstract class UserRepositoryTest extends IntegrationEnvironment {
    @Autowired
    private JdbcClient jdbcClient;

    protected abstract UserRepository getRepository();

    @Test
    @Transactional
    @Rollback
    void insertTest() {
        UserRepository repository = getRepository();
        User user = repository.add(1);

        assertThat(user.getId()).isOne();
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    @Transactional
    @Rollback
    void removeTest() {
        UserRepository repository = getRepository();

        jdbcClient.sql("insert into \"user\" (id, created_at) values (1, now())").update();
        repository.remove(1);
        int count = jdbcClient.sql("select count(*) from \"user\" where id=1")
            .query(int.class)
            .single();

        assertThat(count).isZero();
    }

    @Test
    @Transactional
    @Rollback
    void findTest() {
        UserRepository userRepository = getRepository();
        jdbcClient.sql("insert into \"user\" (id, created_at) values (1, now())").update();

        User user1 = userRepository.find(1);
        User user2 = userRepository.find(2);

        assertThat(user1).isNotNull();
        assertThat(user1.getId()).isOne();
        assertThat(user2).isNull();
    }
}
