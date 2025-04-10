package shigarov.practicum.shopper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shigarov.practicum.shopper.domain.Cart;
import shigarov.practicum.shopper.domain.Order;
import shigarov.practicum.shopper.dto.OrderDto;
import shigarov.practicum.shopper.service.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    public OrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }

    // Оформление заказа (покупка товаров в корзине)
    @PostMapping("/buy")
    public String buyItems() {
        Optional<Cart> cartOptional = cartService.getCart(1L);
        Cart cart = cartOptional.orElseThrow(() -> new NoSuchElementException("Invalid cart"));

        Order order = orderService.buy(cart); // Купить товары в корзине
        cartService.clear(cart); // После покупки товаров корзина очищается

        return "redirect:/orders/" + order.getId() + "?newOrder=true";
    }

    // Список заказов
    @GetMapping("/orders")
    public String showOrders(Model model) {
        Optional<Cart> cartOptional = cartService.getCart(1L);
        Cart cart = cartOptional.orElseThrow(() -> new NoSuchElementException("Invalid cart"));

        List<Order> orders = orderService.getAllOrders(cart);
        List<OrderDto> orderDTOs = new ArrayList<>(orders.size());

        for (Order order : orders) {
            BigDecimal totalCost = orderService.getOrderTotalCost(order);
            OrderDto orderDto = OrderDto.of(order, totalCost);
            orderDTOs.add(orderDto);
        }

        model.addAttribute("orders", orderDTOs);

        return "orders";
    }

    // Карточка заказа
    @GetMapping("/orders/{id}")
    public String showOrder(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean newOrder,
            Model model
    ) {
        Optional<Order> orderOptional = orderService.getOrder(id);
        Order order = orderOptional.orElseThrow(() -> new NoSuchElementException("Invalid order"));
        //OrderDto orderDto = OrderDto.of(order);
        BigDecimal totalCost = orderService.getOrderTotalCost(order);
        OrderDto orderDto = OrderDto.of(order, totalCost);

        model.addAttribute("order", orderDto);
        model.addAttribute("newOrder", newOrder);

        return "order";
    }
}
