package edu.java.scrapper.repository.jpa;

import edu.java.scrapper.model.jpa.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface JpaUserRepository extends JpaRepository<User, Long> {
    @Query(value = "select user_id from user_link where link_id=:linkId", nativeQuery = true)
    List<Long> findUsersByLink(Long linkId);
}
