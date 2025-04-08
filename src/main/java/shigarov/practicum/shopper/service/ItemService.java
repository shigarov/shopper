package shigarov.practicum.shopper.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import shigarov.practicum.shopper.dto.ItemDto;
import shigarov.practicum.shopper.repository.ItemRepository;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Page<ItemDto> getItems(
            @NonNull Long cartId,
            @Nullable String searchTerm,
            @NonNull Pageable pageable
    ) {
        Page<ItemDto> page;
        page = itemRepository.findAll(cartId, searchTerm, pageable);

        return page;
    }

    public ItemDto getItemById(
            @NonNull Long cartId,
            @NonNull Long itemId
    ) {
        // Выбросит NoSuchElementException, если товара нет
        return itemRepository.findById(cartId, itemId).orElseThrow();
    }

}
