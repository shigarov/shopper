package shigarov.practicum.shopper.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CartDetailId implements Serializable {
    private Long cartId;
    private Long itemId;
}
