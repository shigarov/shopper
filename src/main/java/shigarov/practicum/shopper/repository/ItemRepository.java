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
                SELECT new shigarov.practicum.shopper.dto.ItemDto(
                    i,
                    COALESCE((SELECT cd.quantity FROM CartDetail cd
                             WHERE cd.item.id = i.id AND cd.cart.id = :cartId), 0)
                )
                FROM Item i
                WHERE (:searchTerm IS NULL OR
                      i.title LIKE CONCAT('%', :searchTerm, '%') OR
                      i.description LIKE CONCAT('%', :searchTerm, '%'))
            """)
    Page<ItemDto> findAll(
            @NonNull @Param("cartId") Long cartId,
            @Nullable @Param("searchTerm") String searchTerm, // Вернет все товары, если searchTerm = null
            @NonNull Pageable pageable
    );

    @Query("""
                SELECT new shigarov.practicum.shopper.dto.ItemDto(
                    i,
                    COALESCE((SELECT cd.quantity FROM CartDetail cd
                             WHERE cd.item.id = i.id AND cd.cart.id = :cartId), 0)
                )
                FROM Item i
                WHERE i.id = :itemId
            """)
    Optional<ItemDto> findById(
            @Param("cartId") Long cartId,
            @Param("itemId") Long itemId
    );
}
