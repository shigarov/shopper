package shigarov.practicum.shopper.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_details")
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class CartDetail {
    @EmbeddedId
    private CartDetailId id;

    @ManyToOne
    @MapsId("cartId")  // ссылается на поле cartId в CartDetailId
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @MapsId("itemId")  // ссылается на поле itemId в CartDetailId
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    public CartDetail(@NonNull Cart cart, @NonNull Item item, @NonNull Integer quantity, @NonNull BigDecimal price) {
        this.id = new CartDetailId(cart.getId(), item.getId());
        this.cart = cart;
        this.item = item;
        this.quantity = quantity;
        this.price = price;
    }
}