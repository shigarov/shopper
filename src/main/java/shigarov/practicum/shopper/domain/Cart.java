package shigarov.practicum.shopper.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.*;

@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKey(name = "item")
    private Map<Item, CartDetail> details = new HashMap<>();

    public Optional<CartDetail> getCartDetail(@NonNull Item item) {
        CartDetail cartDetail = details.get(item);

        return Optional.ofNullable(cartDetail);
    }

}