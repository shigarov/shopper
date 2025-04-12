package shigarov.practicum.shopper.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import shigarov.practicum.shopper.domain.Cart;
import shigarov.practicum.shopper.domain.CartDetail;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.domain.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByCartId_shouldReturnOrdersForCart() {
        // Arrange
        Cart cart1 = new Cart("session1");
        Cart cart2 = new Cart("session2");
        entityManager.persist(cart1);
        entityManager.persist(cart2);

        Order order1 = new Order(cart1);
        Order order2 = new Order(cart1); // Два заказа для cart1
        Order order3 = new Order(cart2); // Один заказ для cart2
        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.persist(order3);
        entityManager.flush();

        // Act
        List<Order> cart1Orders = orderRepository.findByCartId(cart1.getId());
        List<Order> cart2Orders = orderRepository.findByCartId(cart2.getId());

        // Assert
        assertEquals(2, cart1Orders.size());
        assertEquals(1, cart2Orders.size());
        assertTrue(cart1Orders.stream().allMatch(o -> o.getCart().getId().equals(cart1.getId())));
        assertTrue(cart2Orders.stream().allMatch(o -> o.getCart().getId().equals(cart2.getId())));
    }

    @Test
    void findByCartId_shouldReturnEmptyListForNonExistingCart() {
        // Act
        List<Order> result = orderRepository.findByCartId(999L);

        // Assert
        assertTrue(result.isEmpty());
    }

//    @Test
//    void shouldPersistOrderWithDetails() {
//        // Arrange
//        Cart cart = new Cart("session1");
//        entityManager.persist(cart);
//
//        // Создаем детали корзины
//        Item item1 = new Item(1L, "Item1", "Desc1", "img1.jpg", new BigDecimal("10.00"));
//        Item item2 = new Item(2L, "Item2", "Desc2", "img2.jpg", new BigDecimal("20.00"));
//        entityManager.persist(item1);
//        entityManager.persist(item2);
//
//        CartDetail detail1 = new CartDetail(cart, item1, 1, item1.getPrice());
//        CartDetail detail2 = new CartDetail(cart, item2, 2, item2.getPrice());
//        cart.getDetails().put(item1, detail1);
//        cart.getDetails().put(item2, detail2);
//        entityManager.persist(cart);
//
//        // Act
//        Order order = new Order(cart);
//        Order savedOrder = orderRepository.save(order);
//
//        // Assert
//        assertNotNull(savedOrder.getId());
//        assertEquals(cart.getId(), savedOrder.getCart().getId());
//        assertEquals(2, savedOrder.getDetails().size());
//    }

//    @Test
//    void shouldCascadePersistOrderDetails() {
//        // Arrange
//        Cart cart = new Cart("session1");
//        Item item = new Item(1L, "Item1", "Desc1", "img1.jpg", new BigDecimal("10.00"));
//        entityManager.persist(cart);
//        entityManager.persist(item);
//
//        CartDetail cartDetail = new CartDetail(cart, item, 1, item.getPrice());
//        cart.getDetails().put(item, cartDetail);
//        entityManager.persist(cart);
//
//        // Act
//        Order order = new Order(cart);
//        Order savedOrder = orderRepository.save(order);
//        entityManager.flush();
//        entityManager.clear();
//
//        // Assert
//        Order foundOrder = entityManager.find(Order.class, savedOrder.getId());
//        assertEquals(1, foundOrder.getDetails().size());
//    }

    @Test
    void shouldNotAllowNullCart() {
        // Arrange
        Order order = new Order();
        order.setDetails(Map.of());

        // Act & Assert
        assertThrows(Exception.class, () -> {
            orderRepository.saveAndFlush(order);
        });
    }
}
