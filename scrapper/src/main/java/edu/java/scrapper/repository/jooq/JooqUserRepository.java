package edu.java.scrapper.repository.jooq;

import edu.java.scrapper.model.User;
import edu.java.scrapper.repository.UserRepository;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.scrapper.model.jooq.Tables.USER;

@Repository
@AllArgsConstructor
public class JooqUserRepository implements UserRepository {
    private final DSLContext dslContext;

    @Override
    public User find(long id) {
        return dslContext.selectFrom(USER)
            .where(USER.ID.eq(id))
            .fetchAnyInto(User.class);
    }

    @Override
    public User add(long id) {
        User user = new User(id);
        OffsetDateTime now = OffsetDateTime.now();
        dslContext.insertInto(USER)
            .set(USER.ID, user.getId())
            .set(USER.CREATED_AT, now)
            .execute();
        user.setCreatedAt(now);
        return user;
    }

    @Override
    public void remove(long id) {
        dslContext.deleteFrom(USER)
            .where(USER.ID.eq(id))
            .execute();
    }
}
