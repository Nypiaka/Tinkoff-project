package edu.java.dao.repository;

import edu.java.dao.domain.ContentByLink;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JpaContentRepository extends JpaRepository<ContentByLink, Long> {
    @Transactional
    ContentByLink save(ContentByLink content);

    @Transactional
    Optional<ContentByLink> findByLinkId(Long linkId);

    @Transactional
    @Query(
        value = "select l.link from links l join content_by_link c on c.link_id = l.id where c.updated_at <= "
            + "CURRENT_TIMESTAMP AT TIME ZONE 'MSK' - interval '?1 minute'",
        nativeQuery = true)
    List<String> getAllLinksForInterval(int minutes);

}
