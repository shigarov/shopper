package shigarov.practicum.shopper.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shigarov.practicum.shopper.domain.Cart;
import shigarov.practicum.shopper.domain.CartDetail;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.domain.Order;
import shigarov.practicum.shopper.repository.OrderDetailRepository;
import shigarov.practicum.shopper.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDetailRepository orderDetailRepository;

    @Mock
    private Cart cart;

    @InjectMocks
    private OrderService orderService;

    @Test
    void buy_shouldCreateOrderFromCart() {
        // Arrange
        Long cartId = 1L;
        when(cart.getId()).thenReturn(cartId);

        Item item1 = new Item(1L, "title1", "desc1", "img1.jpg", new BigDecimal("10.00"));
        Item item2 = new Item(2L, "title2", "desc2", "img2.jpg", new BigDecimal("20.00"));

        Map<Item, CartDetail> cartDetails = new HashMap<>();
        cartDetails.put(item1, new CartDetail(cart, item1, 2, new BigDecimal("10.00")));
        cartDetails.put(item2, new CartDetail(cart, item2, 1, new BigDecimal("20.00")));

        when(cart.getDetails()).thenReturn(cartDetails);

        Order expectedOrder = new Order(cart);
        when(orderRepository.save(any(Order.class))).thenReturn(expectedOrder);

        // Act
        Order result = orderService.buy(cart);

        // Assert
        assertNotNull(result);
        assertEquals(cart, result.getCart());
        assertEquals(2, result.getDetails().size());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void getAllOrders_shouldReturnOrdersForCart() {
        // Arrange
        Long cartId = 1L;
        when(cart.getId()).thenReturn(cartId);

        List<Order> expectedOrders = Arrays.asList(
                new Order(cart),
                new Order(cart)
        );

        when(orderRepository.findByCartId(cartId)).thenReturn(expectedOrders);

        // Act
        List<Order> result = orderService.getAllOrders(cart);

        // Assert
        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findByCartId(cartId);
    }

    @Test
    void getOrder_shouldReturnOrderWhenExists() {
        // Arrange
        Long orderId = 1L;
        Order expectedOrder = new Order(cart);
        expectedOrder.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(expectedOrder));

        // Act
        Optional<Order> result = orderService.getOrder(orderId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedOrder, result.get());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void getOrder_shouldReturnEmptyWhenNotExists() {
        // Arrange
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act
        Optional<Order> result = orderService.getOrder(orderId);

        // Assert
        assertTrue(result.isEmpty());
        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    void getOrderTotalCost_shouldReturnSumForOrder() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order(cart);
        order.setId(orderId);

        BigDecimal expectedSum = new BigDecimal("50.00");
        when(orderDetailRepository.sumTotalCostInOrder(orderId)).thenReturn(Optional.of(expectedSum));

        // Act
        BigDecimal result = orderService.getOrderTotalCost(order);

        // Assert
        assertEquals(expectedSum, result);
        verify(orderDetailRepository, times(1)).sumTotalCostInOrder(orderId);
    }

    @Test
    void getOrderTotalCost_shouldReturnZeroWhenNoDetails() {
        // Arrange
        Long orderId = 1L;
        Order order = new Order(cart);
        order.setId(orderId);

        when(orderDetailRepository.sumTotalCostInOrder(orderId)).thenReturn(Optional.empty());

        // Act
        BigDecimal result = orderService.getOrderTotalCost(order);

        // Assert
        assertEquals(BigDecimal.ZERO, result);
        verify(orderDetailRepository, times(1)).sumTotalCostInOrder(orderId);
    }
}