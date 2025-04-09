package shigarov.practicum.shopper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import shigarov.practicum.shopper.domain.CartDetail;
import shigarov.practicum.shopper.domain.CartDetailId;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, CartDetailId> {
    @Transactional
    @Modifying
    @Query("DELETE FROM CartDetails cd WHERE cd.cart.id = :cartId")
    void deleteAllByCartId(@Param("cartId") Long cartId);
}
