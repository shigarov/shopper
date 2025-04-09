package shigarov.practicum.shopper.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_details")
@Data
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
    private Integer quantity = 1;  // лучше задать DEFAULT значение

    public CartDetail(Cart cart, Item item, Integer quantity) {
        this.id = new CartDetailId(cart.getId(), item.getId());
        this.cart = cart;
        this.item = item;
        this.quantity = quantity;
    }
}