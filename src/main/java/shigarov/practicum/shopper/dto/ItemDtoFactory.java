package shigarov.practicum.shopper.dto;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import shigarov.practicum.shopper.domain.Item;

import java.math.BigDecimal;
import java.nio.file.Path;

public class ItemDtoFactory {
    private final Path imagesDir;

    public ItemDtoFactory(@NonNull String imagesDir) {
        // TODO проверить корректность пути
        this.imagesDir = Path.of(imagesDir);
    }

    public ItemDto of(
            @NonNull Item item,
            @NonNull Integer quantity,
            @NonNull BigDecimal price
    ) {
        Long id = item.getId();
        String title = item.getTitle();
        String description = item.getDescription();

        String itemImgPath = item.getImgPath();
        String imgPath;
        if (itemImgPath == null) {
            imgPath = null;
        } else {
            imgPath = imagesDir.resolve(Long.toString(id)).resolve(itemImgPath).toString();
        }

        return new ItemDto(id, title, description, imgPath, quantity, price);
    }

    public ItemDto of(@NonNull Item item, @NonNull Integer quantity) {
        return of(item, quantity, item.getPrice());
    }
}
