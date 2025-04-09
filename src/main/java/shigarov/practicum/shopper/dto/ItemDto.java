package shigarov.practicum.shopper.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import shigarov.practicum.shopper.domain.Item;

import java.math.BigDecimal;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ItemDto {
    private Long id;
    private String title;
    private String description;
    private String imgPath;
    private Integer count;
    private BigDecimal price;

    public static ItemDto of(
            @NonNull Item item,
            @NonNull Integer quantity,
            @NonNull BigDecimal price
    ) {
        Long id = item.getId();
        String title = item.getTitle();
        String description = item.getDescription();
        String imgPath = item.getImgPath();
        ItemDto itemDto = new ItemDto(id, title, description, imgPath, quantity, price);

        return itemDto;
    }

    public static ItemDto of(@NonNull Item item, @NonNull Integer quantity) {
        return ItemDto.of(item, quantity, item.getPrice());
    }
}
