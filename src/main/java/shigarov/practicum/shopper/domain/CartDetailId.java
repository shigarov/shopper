package shigarov.practicum.shopper.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

//@NoArgsConstructor
//@AllArgsConstructor
//@EqualsAndHashCode
//public class CartDetailId implements Serializable {
//    private Long cartId;
//    private Long itemId;
//}

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CartDetailId implements Serializable {
    private Long cartId;
    private Long itemId;
}
