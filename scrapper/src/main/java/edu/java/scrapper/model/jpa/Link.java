package edu.java.scrapper.model.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "link")
@Getter
@Setter
@ToString
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "url", unique = true, updatable = false)
    private String url;

    @Column(name = "last_updated", unique = true, nullable = false)
    private OffsetDateTime updatedAt;

    @ManyToMany(mappedBy = "links")
    @ToString.Exclude
    private Set<User> users = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Link link = (Link) o;
        return Objects.equals(id, link.id)
               && Objects.equals(url, link.url)
               && (updatedAt == null && link.updatedAt == null
                   || updatedAt != null && Objects.equals(
            updatedAt.toInstant().truncatedTo(ChronoUnit.SECONDS),
            link.updatedAt.toInstant().truncatedTo(ChronoUnit.SECONDS)
        ));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, updatedAt);
    }
}
