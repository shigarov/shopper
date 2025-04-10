package shigarov.practicum.shopper.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.repository.ItemRepository;

@Service
public class AdminService {
    private final ItemRepository itemRepository;

    public AdminService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item saveItem(@NonNull Item item) {
        return itemRepository.save(item);
    }

}
