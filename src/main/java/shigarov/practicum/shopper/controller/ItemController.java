package shigarov.practicum.shopper.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shigarov.practicum.shopper.domain.Cart;
import shigarov.practicum.shopper.domain.CartDetail;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.dto.ItemDto;
import shigarov.practicum.shopper.service.*;

import java.util.*;

@Controller
public class ItemController {
    private final int N = 3; // Можно вынести в конфиг

    private final ItemService itemService;
    private final CartService cartService;

    public ItemController(ItemService itemService, CartService cartService) {
        this.itemService = itemService;
        this.cartService = cartService;
    }

    // Просмотр списка всех товаров плиткой на главной странице
    @GetMapping("/main/items")
    public String showItems(
            @RequestParam(name = "search", defaultValue = "") String searchTerm,
            @RequestParam(name = "sort", defaultValue = "NO") SortType sortType,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            Model model
    ) {
        // Создаем Pageable с учетом типа сортировки
        Sort sort = SortType.toSort(sortType);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<Item> page = itemService.getItems(searchTerm, pageable);
        Optional<Cart> cartOptional = cartService.getCart(1L);
        Cart cart = cartOptional.orElseThrow(() -> new NoSuchElementException("Invalid cart"));

        List<Item> items = page.getContent();

        // Разбиваем список товаров на подсписки по N элементов в ряд
        List<List<ItemDto>> itemsInRows = new LinkedList<>();

        int count = 0;
        List<ItemDto> itemsInRow = new ArrayList<>(N);

        for (int i = 0; i < items.size(); i ++) {
            Integer quantity;
            if (count < N) {
                count ++;
                Item item = items.get(i);
                Optional<CartDetail> cartDetailOptional = cart.getCartDetail(item);
                if (cartDetailOptional.isPresent()) {
                    CartDetail cartDetail = cartDetailOptional.get();
                    quantity = cartDetail.getQuantity();
                } else {
                    quantity = 0;
                }
                ItemDto itemDto = ItemDto.of(item, quantity);
                itemsInRow.add(itemDto);
            } else {
                itemsInRows.add(itemsInRow);
                itemsInRow = new ArrayList<>(N);
                count = 0;
            }
        }

        model.addAttribute("items", itemsInRows);
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
        Optional<Item> itemOptional = itemService.getItem(id);
        Item item = itemOptional.orElseThrow(() -> new NoSuchElementException("Invalid item"));

        Optional<Cart> cartOptional = cartService.getCart(1L);
        Cart cart = cartOptional.orElseThrow(() -> new NoSuchElementException("Invalid cart"));

        Optional<CartDetail> cartDetailOptional = cart.getCartDetail(item);

        Integer quantity;

        if (cartDetailOptional.isPresent()) {
            CartDetail cartDetail = cartDetailOptional.get();
            quantity = cartDetail.getQuantity();
        } else {
            quantity = 0;
        }

        ItemDto itemDto = ItemDto.of(item, quantity);
        model.addAttribute("item", itemDto);

        return "item";
    }

}
