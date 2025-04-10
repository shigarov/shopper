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

//    public static ItemDto of(
//            @NonNull Item item,
//            @NonNull Integer quantity,
//            @NonNull BigDecimal price
//    ) {
//        Long id = item.getId();
//        String title = item.getTitle();
//        String description = item.getDescription();
//        String imgPath = imgDir.resolve(item.getImgPath()).toString();
//        ItemDto itemDto = new ItemDto(id, title, description, imgPath, quantity, price);
//
//        return itemDto;
//    }
//
//    public static ItemDto of(@NonNull Item item, @NonNull Integer quantity) {
//        return ItemDto.of(item, quantity, item.getPrice());
//    }
}
