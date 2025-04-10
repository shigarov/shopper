package shigarov.practicum.shopper.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import shigarov.practicum.shopper.domain.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class OrderDto {
    private Long id;
    private List<ItemDto> items;
    private BigDecimal totalCost;

    public Long id() {
        return id;
    }

    public List<ItemDto> items() {
        return items;
    }

    public BigDecimal totalSum() {
        return totalCost;
    }
}
