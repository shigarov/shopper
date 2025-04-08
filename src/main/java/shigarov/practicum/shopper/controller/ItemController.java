package shigarov.practicum.shopper.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shigarov.practicum.shopper.dto.ItemDto;
import shigarov.practicum.shopper.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {
    private final int DEFAULT_ITEMS_PER_ROW = 3; // Можно вынести в конфиг

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    // Просмотр списка всех товаров плиткой на главной странице
    @GetMapping("/main/items")
    public String showItems(
            @RequestParam(name = "search", defaultValue = "") String searchTerm,
            @RequestParam(name = "sort", defaultValue = "NO") SortType sortType,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            Model model) {

        // Создаем Pageable с учетом типа сортировки
        Sort sort = SortType.toSort(sortType);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<ItemDto> page = itemService.getItems(1L, searchTerm, pageable);

        // Разбиваем список товаров на подсписки по N элементов в ряд
        List<ItemDto> items = page.getContent();
        int itemsPerRow = DEFAULT_ITEMS_PER_ROW;
        List<List<ItemDto>> splitItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i += itemsPerRow) {
            int end = Math.min(i + itemsPerRow, items.size());
            splitItems.add(items.subList(i, end));
        }

        model.addAttribute("items", splitItems);
        model.addAttribute("search", searchTerm);
        model.addAttribute("sort", sortType);
        model.addAttribute("paging", Map.of(
                "pageNumber", pageNumber,
                "pageSize", pageSize,
                "hasNext", page.hasNext(),
                "hasPrevious", page.hasPrevious()
        ));

        return "main";
    }

    // Просмотр карточки товара
    @GetMapping("/items/{id}")
    public String showItem(@PathVariable Long id, Model model) {
        ItemDto item = itemService.getItemById(1L, id);
        model.addAttribute("item", item);

        return "item";
    }

}
