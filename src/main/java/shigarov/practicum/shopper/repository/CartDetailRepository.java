package shigarov.practicum.shopper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import shigarov.practicum.shopper.domain.CartDetail;
import shigarov.practicum.shopper.domain.CartDetailId;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, CartDetailId> {
    @Transactional
    @Modifying
    @Query("DELETE FROM CartDetail cd WHERE cd.cart.id = :cartId")
    void deleteAllByCartId(Long cartId);

    @Query("SELECT SUM(cd.price * cd.quantity) FROM CartDetail cd WHERE cd.cart.id = :cartId")
    Optional<BigDecimal> sumTotalCostInCart(@Param("cartId") Long cartId);
}
