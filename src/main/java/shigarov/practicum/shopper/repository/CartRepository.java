package shigarov.practicum.shopper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shigarov.practicum.shopper.domain.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
}
