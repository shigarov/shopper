package shigarov.practicum.shopper.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", unique = true, nullable = false, length = 64)
    private String sessionId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "item")
    private Map<Item, CartDetail> details = new HashMap<>();

    public Cart(String sessionId) {
        this.sessionId = sessionId;
    }

    public Cart(Long id, String sessionId) {
        this.id = id;
        this.sessionId = sessionId;
    }

    public Optional<CartDetail> getCartDetail(@NonNull Item item) {
        CartDetail cartDetail = details.get(item);

        return Optional.ofNullable(cartDetail);
    }

}