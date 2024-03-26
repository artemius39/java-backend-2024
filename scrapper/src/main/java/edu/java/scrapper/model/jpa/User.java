package edu.java.scrapper.model.jpa;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Generated;

@Entity
@Table(name = "`user`")
@Getter @Setter
public class User {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at", nullable = false)
    @Generated(sql = "now()")
    private OffsetDateTime createdAt;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    @JoinTable(
        name = "user_link",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "link_id")
    )
    private Set<Link> links = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id)
               && (createdAt == null && user.createdAt == null
                   || createdAt != null
                      && Objects.equals(
            createdAt.toInstant().truncatedTo(ChronoUnit.SECONDS),
            user.createdAt.toInstant().truncatedTo(ChronoUnit.SECONDS)
        ));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt);
    }
}
