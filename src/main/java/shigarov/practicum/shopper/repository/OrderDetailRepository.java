package shigarov.practicum.shopper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shigarov.practicum.shopper.domain.OrderDetail;
import shigarov.practicum.shopper.domain.OrderDetailId;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
    @Query("SELECT SUM(od.price * od.quantity) FROM OrderDetail od WHERE od.order.id = :orderId")
    Optional<BigDecimal> sumTotalCostInOrder(@Param("orderId") Long orderId);
}
