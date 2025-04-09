package shigarov.practicum.shopper.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_details")
@Data
@EqualsAndHashCode
public class OrderDetail {
    @EmbeddedId
    private OrderDetailId id;

    @ManyToOne
    @MapsId("orderId")  // ссылается на поле orderId в CartDetailId
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @MapsId("itemId")  // ссылается на поле itemId в CartDetailId
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    public OrderDetail(Order order, Item item, Integer quantity, BigDecimal price) {
        this.id = new OrderDetailId(order.getId(), item.getId());
        this.order = order;
        this.item = item;
        this.quantity = quantity;
        this.price = price;
    }
}
