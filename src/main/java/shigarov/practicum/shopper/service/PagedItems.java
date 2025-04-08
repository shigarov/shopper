package shigarov.practicum.shopper.service;

import lombok.Data;
import shigarov.practicum.shopper.domain.Item;

import java.util.List;

@Data
public class PagedItems {
    private List<List<Item>> items;
    private boolean hasNext;
    private boolean hasPrevious;
}
