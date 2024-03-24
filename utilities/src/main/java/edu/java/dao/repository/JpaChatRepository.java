package edu.java.dao.repository;

import edu.java.dao.domain.ChatsToLinks;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JpaChatRepository extends JpaRepository<ChatsToLinks, Long> {
    @Transactional
    ChatsToLinks save(ChatsToLinks chatLink);

    @Transactional
    List<ChatsToLinks> findAllByChatId(Long chatId);

    @Transactional
    boolean existsByChatIdAndLinkId(Long chatId, Long linkId);

    @Transactional
    List<ChatsToLinks> findAllByLinkId(Long linkId);

    @Transactional
    void deleteByChatId(Long chatId);

    @Transactional
    boolean existsByChatId(Long chatId);
}
