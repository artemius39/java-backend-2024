package edu.java.scrapper.repository.jdbc;

import edu.java.scrapper.model.User;
import edu.java.scrapper.repository.UserRepository;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@Primary
@AllArgsConstructor
public class JdbcUserRepository implements UserRepository {
    private final JdbcClient jdbcClient;

    @Override
    public User find(long id) {
        return jdbcClient.sql("select * from \"user\" where id=?")
            .param(id)
            .query(User.class)
            .optional()
            .orElse(null);
    }

    @Override
    public User add(long id) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("insert into \"user\" (id, created_at) values (?, now())")
            .param(id)
            .update(keyHolder);
        Map<String, Object> keys = keyHolder.getKeys();
        assert keys != null;
        Timestamp timestamp = (Timestamp) keys.get("created_at");
        return new User(id, timestamp.toInstant().atOffset(OffsetDateTime.now().getOffset()));
    }

    @Override
    public void remove(long id) {
        jdbcClient.sql("delete from \"user\" where id=?")
            .param(id)
            .update();
    }


}
