package shigarov.practicum.shopper.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import shigarov.practicum.shopper.domain.*;
import shigarov.practicum.shopper.dto.ItemDtoFactory;
import shigarov.practicum.shopper.repository.CartRepository;
import shigarov.practicum.shopper.repository.ItemRepository;
import shigarov.practicum.shopper.repository.OrderRepository;

import java.math.BigDecimal;

import org.springframework.mock.web.MockHttpSession;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class OrderControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

//    private Item item1;
//    private Item item2;
//    private CartDetail cartDetail1;
//    private CartDetail cartDetail2;
//    private Cart cart;

    private String sessionId = "1";

    @Test
    @Transactional
    void buyItems_ShouldRedirectToOrderPage() throws Exception {
        // Подготовка данных корзины
        Item item1 = new Item("Item 1", "Desc 1", "img1.jpg", BigDecimal.ONE);
        itemRepository.save(item1);
        Cart cart = new Cart(sessionId);
        cartRepository.save(cart);

        CartDetail cartDetail = new CartDetail(cart, item1, 2, item1.getPrice());
        cart.getDetails().put(item1, cartDetail);

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        // Act & Assert
        mockMvc.perform(post("/buy").session(mockSession))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @Transactional
    void showOrders_ShouldReturnOrdersView() throws Exception {
        Item item1 = new Item("Item 1", "Desc 1", "img1.jpg", BigDecimal.ONE);
        itemRepository.save(item1);
        Item item2 = new Item("Item 2", "Desc 2", "img2.jpg", BigDecimal.TWO);
        itemRepository.save(item2);

        // Arrange
        Cart cart = new Cart(sessionId);
        cartRepository.save(cart);

        Order order1 = new Order(cart);
        OrderDetail orderDetail1 = new OrderDetail(order1, item1, 1, item1.getPrice());
        order1.getDetails().put(item1, orderDetail1);
        OrderDetail orderDetail2 = new OrderDetail(order1, item2, 2, item1.getPrice());
        order1.getDetails().put(item1, orderDetail2);
        orderRepository.save(order1);

        Order order2 = new Order(cart);
        OrderDetail orderDetail3 = new OrderDetail(order2, item1, 2, item1.getPrice());
        order2.getDetails().put(item1, orderDetail3);
        orderRepository.save(order2);

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        // Act
        mockMvc.perform(get("/orders").session(mockSession))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"));
    }

    @Test
    @Transactional
    void showOrder_ShouldReturnOrderView() throws Exception {
        // Arrange
        Item item1 = new Item("Item 1", "Desc 1", "img1.jpg", BigDecimal.ONE);
        itemRepository.save(item1);
        Item item2 = new Item("Item 2", "Desc 2", "img2.jpg", BigDecimal.TWO);
        itemRepository.save(item2);

        // Arrange
        Cart cart = new Cart(sessionId);
        cartRepository.save(cart);

        Order order1 = new Order(cart);
        OrderDetail orderDetail1 = new OrderDetail(order1, item1, 1, item1.getPrice());
        order1.getDetails().put(item1, orderDetail1);
        OrderDetail orderDetail2 = new OrderDetail(order1, item2, 2, item1.getPrice());
        order1.getDetails().put(item1, orderDetail2);
        order1 = orderRepository.save(order1);
        Long orderId = order1.getId();

        // Act
        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(
                        get("/orders/" + orderId).session(mockSession))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attribute("newOrder", false)
                );
    }

    @Test
    @Transactional
    void showOrder_ShouldThrowNoSuchElementException_WhenOrderNotFound() throws Exception {
        // Arrange
        Cart cart = new Cart(sessionId);
        cartRepository.save(cart);

        // Act & Assert
        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(get("/orders/0").session(mockSession))
                .andExpect(status().isNotFound());
    }
}
