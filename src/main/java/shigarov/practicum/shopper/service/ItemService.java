package shigarov.practicum.shopper.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.repository.ItemRepository;

import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Page<Item> getItems(@Nullable String searchTerm, @NonNull Pageable pageable) {
        return itemRepository.findAllBySearchTerm(searchTerm, pageable);
    }

    public Optional<Item> getItem(@NonNull Long itemId) {
        return itemRepository.findById(itemId);
    }

}
