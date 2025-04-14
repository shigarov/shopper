package shigarov.practicum.shopper.dto;

import lombok.*;

import shigarov.practicum.shopper.domain.Item;

import java.math.BigDecimal;
import java.nio.file.Path;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Data
public class ItemDto {
    private Long id;
    private String title;
    private String description;
    private String imgPath;
    private Integer count;
    private BigDecimal price;
}
