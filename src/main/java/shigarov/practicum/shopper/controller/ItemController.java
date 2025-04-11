package shigarov.practicum.shopper.controller;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import shigarov.practicum.shopper.dto.ItemDtoFactory;
import shigarov.practicum.shopper.types.SortType;
import shigarov.practicum.shopper.domain.Cart;
import shigarov.practicum.shopper.domain.CartDetail;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.dto.ItemDto;
import shigarov.practicum.shopper.dto.PagingDto;
import shigarov.practicum.shopper.service.*;

import java.nio.file.Path;
import java.util.*;

@Controller
public class ItemController {
    // Тот самый N (число товаров показываемых в ряд)
    private final int N = 3; // TODO Вынести в конфиг

    // Относительный путь к директории с изображениями товаров
    @Value("${storage.imagesDir}")
    private String imagesDir;

    private final ItemService itemService;
    private final CartService cartService;

    public ItemController(ItemService itemService, CartService cartService) {
        this.itemService = itemService;
        this.cartService = cartService;
    }

    // Фабрика DTO товаров
    private ItemDtoFactory itemDtoFactory;

    @PostConstruct
    private void postConstruct() {
        if (imagesDir == null)
            throw new IllegalStateException("Invalid images directory");
        else
            itemDtoFactory = new ItemDtoFactory(imagesDir);
    }

    // Просмотр списка всех товаров плиткой на главной странице
    @GetMapping("/main/items")
    public String showItems(
            @RequestParam(name = "search", defaultValue = "") String searchTerm,
            @RequestParam(name = "sort", defaultValue = "NO") SortType sortType,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            Model model,
            HttpSession session
    ) {
        // Создаем Pageable с учетом типа сортировки
        Sort sort = SortType.toSort(sortType);
        int zeroBasedPageNumber = -- pageNumber;
        Pageable pageable = PageRequest.of(zeroBasedPageNumber, pageSize, sort);

        Page<Item> page = itemService.getItems(searchTerm, pageable);

        String sessionId = session.getId();
        Cart cart = cartService.getOrCreateCartBySessionId(sessionId);

        //Optional<Cart> cartOptional = cartService.getCart(1L);
        //Cart cart = cartOptional.orElseThrow(() -> new NoSuchElementException("Invalid cart"));

        List<Item> items = page.getContent();

        // Разбиваем список товаров на подсписки по N элементов в ряд
        List<List<ItemDto>> itemsInRows = new LinkedList<>();

        int count = 0;
        List<ItemDto> itemsInRow = new ArrayList<>(N);
        itemsInRows.add(itemsInRow);

        for (int i = 0; i < items.size(); i ++) {
            Integer quantity;
            Item item = items.get(i);
            Optional<CartDetail> cartDetailOptional = cart.getCartDetail(item);
            if (cartDetailOptional.isPresent()) {
                CartDetail cartDetail = cartDetailOptional.get();
                quantity = cartDetail.getQuantity();
            } else {
                quantity = 0;
            }
            Path imagesDirPath = Path.of(imagesDir);
            ItemDto itemDto = itemDtoFactory.of(item, quantity);
            itemsInRow.add(itemDto);

            if (count < N - 1) {
                count ++;
            } else {
                count = 0;
                itemsInRow = new ArrayList<>(N);
                itemsInRows.add(itemsInRow);
            }
        }

        model.addAttribute("items", itemsInRows);
        model.addAttribute("search", searchTerm);
        model.addAttribute("sort", sortType);

        PagingDto pagingDto = PagingDto.of(page);
        model.addAttribute("paging", pagingDto);

        return "main";
    }

    // Просмотр карточки товара
    @GetMapping("/items/{id}")
    public String showItem(@PathVariable Long id, Model model, HttpSession session) {
        Optional<Item> itemOptional = itemService.getItem(id);
        Item item = itemOptional.orElseThrow(() -> new NoSuchElementException("Invalid item"));

        //Optional<Cart> cartOptional = cartService.getCart(1L);
        //Cart cart = cartOptional.orElseThrow(() -> new NoSuchElementException("Invalid cart"));

        String sessionId = session.getId();
        Cart cart = cartService.getOrCreateCartBySessionId(sessionId);

        Optional<CartDetail> cartDetailOptional = cart.getCartDetail(item);

        Integer quantity;

        if (cartDetailOptional.isPresent()) {
            CartDetail cartDetail = cartDetailOptional.get();
            quantity = cartDetail.getQuantity();
        } else {
            quantity = 0;
        }

        ItemDto itemDto = itemDtoFactory.of(item, quantity);
        model.addAttribute("item", itemDto);

        return "item";
    }

}
