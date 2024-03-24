package edu.java.dao.repository;

import edu.java.dao.domain.Link;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLinksRepository extends JpaRepository<Link, Long> {

    Link save(String link);

    void deleteByLink(String link);

    Optional<Link> findByLink(String link);

}
