package shigarov.practicum.shopper.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import shigarov.practicum.shopper.domain.Cart;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.domain.Order;
import shigarov.practicum.shopper.domain.OrderDetail;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderDetailRepositoryTest {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void sumTotalCostInOrder_shouldCalculateCorrectTotal() {
        // Arrange
        Order order = new Order(new Cart("session1"));
        Item item1 = new Item(1L, "Item1", "Desc1", "img1.jpg", new BigDecimal("10.00"));
        Item item2 = new Item(2L, "Item2", "Desc2", "img2.jpg", new BigDecimal("20.00"));

        entityManager.persist(order.getCart());
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(order);

        OrderDetail detail1 = new OrderDetail(order, item1, 2, item1.getPrice()); // 10 * 2 = 20
        OrderDetail detail2 = new OrderDetail(order, item2, 3, item2.getPrice()); // 20 * 3 = 60
        entityManager.persist(detail1);
        entityManager.persist(detail2);
        entityManager.flush();

        // Act
        Optional<BigDecimal> result = orderDetailRepository.sumTotalCostInOrder(order.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("80.00"), result.get());
    }

    @Test
    void sumTotalCostInOrder_shouldReturnEmptyForEmptyOrder() {
        // Arrange
        Order order = new Order(new Cart("session1"));
        entityManager.persist(order.getCart());
        entityManager.persist(order);
        entityManager.flush();

        // Act
        Optional<BigDecimal> result = orderDetailRepository.sumTotalCostInOrder(order.getId());

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void shouldPersistOrderDetailWithCompositeKey() {
        // Arrange
        Order order = new Order(new Cart("session1"));
        Item item = new Item(1L, "Item1", "Desc1", "img1.jpg", new BigDecimal("10.00"));

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
    void shouldNotAllowDuplicateCompositeKeys() {
        // Arrange
        Order order = new Order(new Cart("session1"));
        Item item = new Item(1L, "Item1", "Desc1", "img1.jpg", new BigDecimal("10.00"));

        entityManager.persist(order.getCart());
        entityManager.persist(item);
        entityManager.persist(order);

        OrderDetail detail1 = new OrderDetail(order, item, 1, item.getPrice());
        orderDetailRepository.save(detail1);
        entityManager.flush();

        // Act & Assert
        OrderDetail detail2 = new OrderDetail(order, item, 2, item.getPrice());
        assertThrows(Exception.class, () -> {
            orderDetailRepository.saveAndFlush(detail2);
        });
    }

    @Test
    void shouldNotAllowNullOrderOrItem() {
        // Arrange
        Order order = new Order(new Cart("session1"));
        Item item = new Item(1L, "Item1", "Desc1", "img1.jpg", new BigDecimal("10.00"));

        entityManager.persist(order.getCart());
        entityManager.persist(item);
        entityManager.persist(order);

        // Act & Assert for null order
        OrderDetail detailWithNullOrder = new OrderDetail(null, item, 1, item.getPrice());
        assertThrows(Exception.class, () -> {
            orderDetailRepository.saveAndFlush(detailWithNullOrder);
        });

        // Act & Assert for null item
        OrderDetail detailWithNullItem = new OrderDetail(order, null, 1, item.getPrice());
        assertThrows(Exception.class, () -> {
            orderDetailRepository.saveAndFlush(detailWithNullItem);
        });
    }

    @Test
    void shouldNotAllowNegativeQuantity() {
        // Arrange
        Order order = new Order(new Cart("session1"));
        Item item = new Item(1L, "Item1", "Desc1", "img1.jpg", new BigDecimal("10.00"));

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
        Item item = new Item(1L, "Item1", "Desc1", "img1.jpg", new BigDecimal("10.00"));

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
