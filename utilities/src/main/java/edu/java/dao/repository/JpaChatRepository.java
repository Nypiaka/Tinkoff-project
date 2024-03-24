package edu.java.dao.repository;

import edu.java.dao.domain.ChatsToLinks;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaChatRepository extends JpaRepository<ChatsToLinks, Long> {
    ChatsToLinks save(ChatsToLinks chatLink);

    List<ChatsToLinks> findAllByChatId(Long chatId);

    boolean existsByChatIdAndLinkId(Long chatId, Long linkId);

    List<ChatsToLinks> findAllByLinkId(Long linkId);

}
