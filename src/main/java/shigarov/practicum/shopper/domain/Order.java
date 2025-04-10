package shigarov.practicum.shopper.domain;

import lombok.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "item")
    private Map<Item, OrderDetail> details = new HashMap<>();

    public Order(@NonNull Cart cart) {
        this.cart = cart;

        Collection<CartDetail> cartDetails = cart.getDetails().values();

        for (CartDetail cartDetail : cartDetails) {
            Item item = cartDetail.getItem();
            Integer quantity = cartDetail.getQuantity();
            BigDecimal price = cartDetail.getPrice();

            OrderDetail orderDetail = new OrderDetail(this, item, quantity, price);
            details.put(item, orderDetail);
        }
    }

//    public BigDecimal totalCost() {
//        BigDecimal totalCost = BigDecimal.ZERO;
//        Collection<OrderDetail> orderDetails = details.values();
//
//        for (OrderDetail orderDetail : orderDetails) {
//            BigDecimal quantity = BigDecimal.valueOf(orderDetail.getQuantity());
//            BigDecimal price = orderDetail.getPrice();
//            BigDecimal cost = price.multiply(quantity);
//            totalCost.add(cost);
//        }
//
//        return totalCost;
//    }
}