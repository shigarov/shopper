package shigarov.practicum.shopper.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.util.NestedServletException;

import jakarta.servlet.http.HttpSession;
import shigarov.practicum.shopper.domain.Cart;
import shigarov.practicum.shopper.domain.CartDetail;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.domain.Order;
import shigarov.practicum.shopper.dto.ItemDtoFactory;
import shigarov.practicum.shopper.dto.OrderDto;
import shigarov.practicum.shopper.dto.OrderDtoFactory;
import shigarov.practicum.shopper.service.CartService;
import shigarov.practicum.shopper.service.OrderService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private HttpSession session;

    @MockitoBean
    private Model model;

    private final String sessionId = "1";
    private Cart testCart;
    private Item testItem;

    private ItemDtoFactory itemDtoFactory = new ItemDtoFactory("images-test");
    private OrderDtoFactory orderDtoFactory = new OrderDtoFactory(itemDtoFactory);

    @Test
    void buyItems_ShouldRedirectToOrderPage() throws Exception {
        // Подготовка данных корзины
        testItem = new Item(1L, "Item", "Desc", "img.jpg", BigDecimal.ONE);
        testCart = new Cart(1L, sessionId);

        when(session.getId()).thenReturn(sessionId);
        when(cartService.getOrCreateCartBySessionId(sessionId)).thenReturn(testCart);

        CartDetail cartDetail = new CartDetail(testCart, testItem, 2, testItem.getPrice());
        testCart.getDetails().put(testItem, cartDetail);

        Order order = new Order(testCart);
        order.setId(1L);

        when(session.getId()).thenReturn(sessionId);
        when(cartService.getOrCreateCartBySessionId(sessionId)).thenReturn(testCart);
        when(orderService.buy(testCart)).thenReturn(order);

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        // Act & Assert
        mockMvc.perform(post("/buy").session(mockSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/1?newOrder=true"));

        verify(cartService).clear(testCart);
    }

    @Test
    void showOrders_ShouldReturnOrdersView() throws Exception {
        // Arrange
        Cart cart = new Cart();
        List<Order> orders = new ArrayList<>();
        Order order = new Order();
        orders.add(order);
        OrderDto orderDto = orderDtoFactory.of(order, BigDecimal.ZERO);

        when(session.getId()).thenReturn(sessionId);
        when(cartService.getOrCreateCartBySessionId(sessionId)).thenReturn(cart);
        when(orderService.getAllOrders(cart)).thenReturn(orders);
        when(orderService.getOrderTotalCost(order)).thenReturn(BigDecimal.ZERO);

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        // Act
        mockMvc.perform(get("/orders").session(mockSession))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attribute("orders", List.of(orderDto)));
    }

    @Test
    void showOrder_ShouldReturnOrderView() throws Exception {
        // Arrange
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);
        OrderDto orderDto = orderDtoFactory.of(order, BigDecimal.ZERO);

        when(orderService.getOrder(orderId)).thenReturn(Optional.of(order));
        when(orderService.getOrderTotalCost(order)).thenReturn(BigDecimal.TEN);

        // Act
        //String viewName = orderController.showOrder(orderId, false, model);
        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(
                get("/orders/1").session(mockSession))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attribute("newOrder", false)
                );
    }

    @Test
    void showOrder_ShouldThrowNoSuchElementException_WhenOrderNotFound() throws Exception {
        // Arrange
        when(orderService.getOrder(0L)).thenReturn(Optional.empty());

        // Act & Assert
        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(
                get("/orders/0").session(mockSession))
                .andExpect(status().isNotFound());
    }
}
