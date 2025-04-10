package shigarov.practicum.shopper.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.dto.ItemDto;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("""
            SELECT i FROM Item i WHERE
            (:searchTerm IS NULL OR
            LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
            LOWER(i.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
            """)
    Page<Item> findAll(
            @Nullable @Param("searchTerm") String searchTerm,
            @NonNull Pageable pageable
    );

    boolean existsByTitle(String title);
}
