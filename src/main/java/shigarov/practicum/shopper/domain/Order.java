package shigarov.practicum.shopper.domain;

import lombok.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "orders")
@Data
@EqualsAndHashCode
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderDetail> details = new HashSet<>();

    private Order() {}

    public static Order of(@NonNull Cart cart) {
        Order order = new Order();
        Set<OrderDetail> orderDetails = order.getDetails();

        for (CartDetail cartDetail : cart.getDetails()) {
            Item item = cartDetail.getItem();
            Integer quantity = cartDetail.getQuantity();
            BigDecimal price = item.getPrice();

            OrderDetail orderDetail = new OrderDetail(order, item, quantity, price);
            orderDetails.add(orderDetail);
        }

        return order;
    }
}