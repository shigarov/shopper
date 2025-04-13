package shigarov.practicum.shopper.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import shigarov.practicum.shopper.domain.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class OrderDetailRepositoryTest {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @Transactional
    void sumTotalCostInOrder_shouldCalculateCorrectTotal() {
        // Arrange
        Cart cart = new Cart("1");
        Cart savedCart = entityManager.persist(cart);

        Item item1 = new Item("title1", "desc1", "img1.jpg", BigDecimal.ONE);
        Item item2 = new Item("title2", "desc2", "img2.jpg", BigDecimal.TWO);
        entityManager.persist(item1);
        entityManager.persist(item2);

        Order order = new Order(savedCart);

        entityManager.persist(order.getCart());

        entityManager.persist(order);

        OrderDetail orderDetail1 = new OrderDetail(order, item1, 2, item1.getPrice()); // 1 * 2 = 2
        OrderDetail orderDetail2 = new OrderDetail(order, item2, 3, item2.getPrice()); // 2 * 3 = 6
        entityManager.persist(orderDetail1);
        entityManager.persist(orderDetail2);
        entityManager.flush();

        entityManager.persist(order);

        // Act
        Optional<BigDecimal> result = orderDetailRepository.sumTotalCostInOrder(order.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("8.00"), result.get());
    }

    @Test
    @Transactional
    void sumTotalCostInOrder_shouldReturnEmptyForEmptyOrder() {
        // Arrange
        Order order = new Order(new Cart("1"));
        entityManager.persist(order.getCart());
        entityManager.persist(order);
        entityManager.flush();

        // Act
        Optional<BigDecimal> result = orderDetailRepository.sumTotalCostInOrder(order.getId());

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @Transactional
    void shouldPersistOrderDetailWithCompositeKey() {
        // Arrange
        Order order = new Order(new Cart("session1"));
        Item item = new Item("title1", "desc1", "img1.jpg", BigDecimal.ONE);

        entityManager.persist(order.getCart());
        entityManager.persist(item);
        entityManager.persist(order);

        OrderDetail detail = new OrderDetail(order, item, 1, item.getPrice());

        // Act
        OrderDetail savedDetail = orderDetailRepository.save(detail);
        entityManager.flush();

        // Assert
        assertNotNull(savedDetail);
        assertEquals(order.getId(), savedDetail.getId().getOrderId());
        assertEquals(item.getId(), savedDetail.getId().getItemId());
    }

    @Test
    void shouldNotAllowNegativeQuantity() {
        // Arrange
        Order order = new Order(new Cart("session1"));
        Item item = new Item("title1", "desc1", "img1.jpg", BigDecimal.ONE);

        entityManager.persist(order.getCart());
        entityManager.persist(item);
        entityManager.persist(order);

        OrderDetail detail = new OrderDetail(order, item, -1, item.getPrice());

        // Act & Assert
        assertThrows(Exception.class, () -> {
            orderDetailRepository.saveAndFlush(detail);
        });
    }

    @Test
    void shouldNotAllowNegativePrice() {
        // Arrange
        Order order = new Order(new Cart("session1"));
        Item item = new Item("title1", "desc1", "img1.jpg", BigDecimal.ONE);

        entityManager.persist(order.getCart());
        entityManager.persist(item);
        entityManager.persist(order);

        OrderDetail detail = new OrderDetail(order, item, 1, new BigDecimal("-5.00"));

        // Act & Assert
        assertThrows(Exception.class, () -> {
            orderDetailRepository.saveAndFlush(detail);
        });
    }
}
