package edu.java.dao.repository;

import edu.java.dao.domain.Link;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JpaLinksRepository extends JpaRepository<Link, Long> {
    @Transactional
    Link save(String link);

    @Transactional
    void deleteByLink(String link);

    @Transactional
    Optional<Link> findByLink(String link);

}
