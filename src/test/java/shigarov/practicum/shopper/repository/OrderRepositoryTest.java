package shigarov.practicum.shopper.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import shigarov.practicum.shopper.domain.Cart;
import shigarov.practicum.shopper.domain.CartDetail;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.domain.Order;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @Transactional
    void findByCartId_shouldReturnOrdersForCart() {
        // Arrange
        Cart cart1 = new Cart("1");
        Cart cart2 = new Cart("2");
        Cart savedCart1 = entityManager.persist(cart1);
        Cart savedCart2 = entityManager.persist(cart2);

        Order order1 = new Order(savedCart1);
        Order order2 = new Order(savedCart1); // Два заказа для cart1
        Order order3 = new Order(savedCart2); // Один заказ для cart2
        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.persist(order3);
        entityManager.flush();

        // Act
        List<Order> orderList1 = orderRepository.findByCartId(cart1.getId());
        List<Order> orderList2 = orderRepository.findByCartId(cart2.getId());

        // Assert
        assertEquals(2, orderList1.size());
        assertEquals(1, orderList2.size());
        assertTrue(orderList1.stream().allMatch(o -> o.getCart().getId().equals(cart1.getId())));
        assertTrue(orderList2.stream().allMatch(o -> o.getCart().getId().equals(cart2.getId())));
    }

    @Test
    void findByCartId_shouldReturnEmptyListForNonExistingCart() {
        // Act
        List<Order> result = orderRepository.findByCartId(0L);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @Transactional
    void shouldPersistOrderWithDetails() {
        // Arrange
        Cart cart = new Cart("1");
        Item item1 = new Item("title1", "desc1", "img1.jpg", BigDecimal.ONE);
        Item item2 = new Item("title2", "desc2", "img2.jpg", BigDecimal.TWO);

        // Создаем детали корзины
        entityManager.persist(item1);
        entityManager.persist(item2);

        CartDetail detail1 = new CartDetail(cart, item1, 1, item1.getPrice());
        CartDetail detail2 = new CartDetail(cart, item2, 2, item2.getPrice());
        cart.getDetails().put(item1, detail1);
        cart.getDetails().put(item2, detail2);
        entityManager.persist(cart);

        // Act
        Order order = new Order(cart);
        Order savedOrder = orderRepository.save(order);

        // Assert
        assertNotNull(savedOrder.getId());
        assertEquals(cart.getId(), savedOrder.getCart().getId());
        assertEquals(2, savedOrder.getDetails().size());
    }

    @Test
    @Transactional
    void shouldCascadePersistOrderDetails() {
        // Arrange
        Cart cart = new Cart("1");
        Item item = new Item("title", "desc1", "img1.jpg", BigDecimal.ONE);
        entityManager.persist(cart);
        entityManager.persist(item);

        CartDetail cartDetail = new CartDetail(cart, item, 1, item.getPrice());
        cart.getDetails().put(item, cartDetail);
        entityManager.persist(cart);

        // Act
        Order order = new Order(cart);
        Order savedOrder = orderRepository.save(order);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Order foundOrder = entityManager.find(Order.class, savedOrder.getId());
        assertEquals(1, foundOrder.getDetails().size());
    }

    @Test
    void shouldNotAllowNullCart() {
        // Arrange
        Order order = new Order();

        // Act & Assert
        assertThrows(Exception.class, () -> {
            orderRepository.saveAndFlush(order);
        });
    }
}
