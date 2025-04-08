package shigarov.practicum.shopper.controller;

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
import shigarov.practicum.shopper.service.ActionType;
import shigarov.practicum.shopper.service.CartService;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Просмотр корзины
    @GetMapping("/cart/items")
    public String showCart(Model model) {
        Optional<Cart> cartOptional = cartService.getCart(1L);
        Cart cart = cartOptional.orElseThrow(() -> new NoSuchElementException("Invalid cart"));
        Set<CartDetail> cartDetails = cart.getDetails();

        List<ItemDto> items = new ArrayList<>(cartDetails.size());
        for(CartDetail cartDetail : cartDetails) {
            Item item = cartDetail.getItem();
            Integer quantity = cartDetail.getQuantity();
            ItemDto itemDto = ItemDto.of(item, quantity);
            items.add(itemDto);
        }

        double total = cartService.calculateTotalCost(cart).doubleValue();

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("empty", items.isEmpty());

        return "cart";
    }

    // Изменение количества товара в корзине с главной страницы
    @PostMapping("/main/items/{id}")
    public String updateCartByMainPage(
            @PathVariable Long id,
            @RequestParam ActionType action,
            RedirectAttributes redirectAttributes) {

        cartService.updateCart(1L, id, action);

        return "redirect:/main/items";
    }

    // Изменение количества товара в корзине (со страницы корзины)
    @PostMapping("/cart/items/{id}")
    public String updateCartByCartPage(
            @PathVariable Long id,
            @RequestParam ActionType action) {

        cartService.updateCart(1L, id, action);

        return "redirect:/cart/items";
    }

    // Изменение количества товара в корзине (со страницы товара)
    @PostMapping("/items/{id}")
    public String updateCartByItemPage(
            @PathVariable Long id,
            @RequestParam ActionType action) {

        cartService.updateCart(1L, id, action);

        return "redirect:/items/" + id;
    }

}
