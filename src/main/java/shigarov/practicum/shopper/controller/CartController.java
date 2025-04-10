package shigarov.practicum.shopper.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import shigarov.practicum.shopper.domain.Cart;
import shigarov.practicum.shopper.domain.CartDetail;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.dto.ItemDto;
import shigarov.practicum.shopper.dto.ItemDtoFactory;
import shigarov.practicum.shopper.types.ActionType;
import shigarov.practicum.shopper.service.CartService;
import shigarov.practicum.shopper.service.ItemService;

import java.math.BigDecimal;
import java.util.*;

import jakarta.annotation.PostConstruct;

@Controller
public class CartController {

    // Относительный путь к директории с изображениями товаров
    @Value("${storage.imagesDir}")
    private String imagesDir;

    private final CartService cartService;
    private final ItemService itemService;

    public CartController(CartService cartService, ItemService itemService) {
        this.cartService = cartService;
        this.itemService = itemService;
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

    // Список товаров в корзине
    @GetMapping("/cart/items")
    public String showCart(Model model) {
        Optional<Cart> cartOptional = cartService.getCart(1L);
        Cart cart = cartOptional.orElseThrow(() -> new NoSuchElementException("Invalid cart"));

        Collection<CartDetail> cartDetails = cart.getDetails().values();

        List<ItemDto> items = new ArrayList<>(cartDetails.size());
        for(CartDetail cartDetail : cartDetails) {
            Item item = cartDetail.getItem();
            Integer quantity = cartDetail.getQuantity();
            ItemDto itemDto = itemDtoFactory.of(item, quantity);
            items.add(itemDto);
        }

        // Подсчет итоговой суммы корзины
        BigDecimal totalCost = cartService.getCartTotalCost(cart);

        model.addAttribute("items", items);
        model.addAttribute("total", totalCost);
        model.addAttribute("empty", items.isEmpty());

        return "cart";
    }

    private void updateCart(Long cartId, Long itemId, ActionType action) {
        Optional<Item> itemOptional = itemService.getItem(itemId);
        Item item = itemOptional.orElseThrow(() -> new NoSuchElementException("Invalid item"));

        Optional<Cart> cartOptional = cartService.getCart(cartId);
        Cart cart = cartOptional.orElseThrow(() -> new NoSuchElementException("Invalid cart"));

        cartService.updateCart(cart, item, action);
    }

    // Изменение количества товара в корзине с главной страницы
    @PostMapping("/main/items/{id}")
    public String updateCartByMainPage(
            @PathVariable Long id,
            @RequestParam ActionType action,
            RedirectAttributes redirectAttributes) {

        updateCart(1L, id, action);

        return "redirect:/main/items";
    }

    // Изменение количества товара в корзине (со страницы корзины)
    @PostMapping("/cart/items/{id}")
    public String updateCartByCartPage(
            @PathVariable Long id,
            @RequestParam ActionType action) {

        updateCart(1L, id, action);

        return "redirect:/cart/items";
    }

    // Изменение количества товара в корзине (со страницы товара)
    @PostMapping("/items/{id}")
    public String updateCartByItemPage(
            @PathVariable Long id,
            @RequestParam ActionType action) {

        updateCart(1L, id, action);

        return "redirect:/items/" + id;
    }

}
