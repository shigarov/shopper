package shigarov.practicum.shopper.controller;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shigarov.practicum.shopper.domain.Cart;
import shigarov.practicum.shopper.domain.Order;
import shigarov.practicum.shopper.dto.ItemDtoFactory;
import shigarov.practicum.shopper.dto.OrderDto;
import shigarov.practicum.shopper.dto.OrderDtoFactory;
import shigarov.practicum.shopper.service.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
public class OrderController {
    // Относительный путь к директории с изображениями товаров
    @Value("${storage.images-dir}")
    private String imagesDir;

    private final OrderService orderService;
    private final CartService cartService;

    public OrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }

    // Фабрика DTO заказов
    private OrderDtoFactory orderDtoFactory;

    @PostConstruct
    public void postConstruct() {
        if (imagesDir == null)
            throw new IllegalStateException("Invalid images directory");
        else
            orderDtoFactory = new OrderDtoFactory(new ItemDtoFactory(imagesDir));
    }

    // Оформление заказа (покупка товаров в корзине)
    @PostMapping("/buy")
    public String buyItems(HttpSession session) {
        String sessionId = session.getId();
        Cart cart = cartService.getOrCreateCartBySessionId(sessionId);

        Order order = orderService.buy(cart); // Купить товары в корзине
        cartService.clear(cart); // После покупки товаров корзина очищается

        return "redirect:/orders/" + order.getId() + "?newOrder=true";
    }

    // Список заказов
    @GetMapping("/orders")
    public String showOrders(Model model, HttpSession session) {
        String sessionId = session.getId();
        Cart cart = cartService.getOrCreateCartBySessionId(sessionId);

        List<Order> orders = orderService.getAllOrders(cart);
        List<OrderDto> orderDTOs = new ArrayList<>(orders.size());

        for (Order order : orders) {
            BigDecimal totalCost = orderService.getOrderTotalCost(order);
            OrderDto orderDto = orderDtoFactory.of(order, totalCost);
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
    ) throws NoSuchElementException {
        Optional<Order> orderOptional = orderService.getOrder(id);
        Order order = orderOptional.orElseThrow(() -> new NoSuchElementException("Invalid order"));
        BigDecimal totalCost = orderService.getOrderTotalCost(order);
        OrderDto orderDto = orderDtoFactory.of(order, totalCost);

        model.addAttribute("order", orderDto);
        model.addAttribute("newOrder", newOrder);

        return "order";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElement(NoSuchElementException e) {
        return ResponseEntity.notFound().build();
    }
}
