package edu.java.scrapper.repository.jdbc;

import edu.java.scrapper.model.Link;
import edu.java.scrapper.repository.LinkRepository;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class JdbcLinkRepository implements LinkRepository {
    private final JdbcClient jdbcClient;

    @Override
    public Collection<Link> findByUserId(Long userId) {
        return jdbcClient.sql(
                "select link.id, link.url, link.last_updated "
                + "from user_link "
                + "join link on user_link.link_id = link.id "
                + "where user_id=?"
            )
            .param(userId)
            .query((rs, rn) -> toLink(rs))
            .list();
    }

    @Override
    public Collection<Link> findByLastUpdateTime(Duration timeSinceLastUpdate) {
        return jdbcClient.sql("select * from link where last_updated < ?")
            .param(LocalDateTime.now().minus(timeSinceLastUpdate))
            .query((rs, rn) -> toLink(rs))
            .list();
    }

    @Override
    public List<Long> findUserIdsByLinkId(Long id) {
        return jdbcClient.sql("select user_id from user_link where link_id=?")
            .param(id)
            .query(Long.class)
            .list();
    }

    @SuppressWarnings("MagicNumber")
    private Link toLink(ResultSet resultSet) throws SQLException {
        return new Link(
            resultSet.getLong(1),
            URI.create(resultSet.getString(2)),
            resultSet.getTimestamp(3).toInstant().atOffset(OffsetDateTime.now().getOffset())
        );
    }

    public int tryAdd(long userId, long linkId) {
        return jdbcClient.sql("insert into user_link (user_id, link_id) values (?, ?) on conflict do nothing")
            .param(userId)
            .param(linkId)
            .update();
    }

    public int tryRemove(long userId, long linkId) {
        return jdbcClient.sql("delete from user_link where link_id=? and user_id=?")
            .param(linkId)
            .param(userId)
            .update();
    }

    public Link add(URI url) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("insert into link (url, last_updated) values (?, now())")
            .param(url.toString())
            .update(keyHolder);
        Map<String, Object> keys = keyHolder.getKeys();
        assert keys != null;
        Long id = (Long) keys.get("id");
        Timestamp timestamp = (Timestamp) keys.get("last_updated");
        OffsetDateTime lastUpdated = OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC);
        return new Link(id, url, lastUpdated);
    }

    public void remove(Link link) {
        jdbcClient.sql("delete from link where id=?")
            .param(link.getId())
            .update();
    }

    public Long countByLinkId(long linkId) {
        return jdbcClient.sql("select count(*) from user_link where link_id=?")
            .param(linkId)
            .query(Long.class)
            .single();
    }

    public Link findByUrl(URI url) {
        return jdbcClient.sql("select id, url, last_updated from link where url=?")
            .param(url.toString())
            .query((rs, rn) -> toLink(rs))
            .single();
    }

    public void update(Link link, OffsetDateTime updateTime) {
        jdbcClient.sql("update link set last_updated=? where id=?")
            .param(updateTime)
            .param(link.getId())
            .update();
    }

    @Override
    public void removeAllByUserId(long userId) {
        jdbcClient.sql("delete from user_link where user_id=?")
            .param(userId)
            .update();
    }

    @Override
    public void removeAllUntracked() {
        jdbcClient.sql("delete from link where id not in (select distinct link_id from user_link)")
            .update();
    }
}
