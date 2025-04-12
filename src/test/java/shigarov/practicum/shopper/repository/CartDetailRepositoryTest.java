package shigarov.practicum.shopper.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import shigarov.practicum.shopper.domain.Cart;
import shigarov.practicum.shopper.domain.CartDetail;
import shigarov.practicum.shopper.domain.Item;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CartDetailRepositoryTest {

    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void deleteAllByCartId_shouldRemoveAllDetailsForCart() {
        // Arrange
        Cart cart = new Cart("session1");
        Item item1 = new Item(1L, "Item1", "Desc1", "img1.jpg", new BigDecimal("10.00"));
        Item item2 = new Item(2L, "Item2", "Desc2", "img2.jpg", new BigDecimal("20.00"));

        entityManager.persist(cart);
        entityManager.persist(item1);
        entityManager.persist(item2);

        CartDetail detail1 = new CartDetail(cart, item1, 1, item1.getPrice());
        CartDetail detail2 = new CartDetail(cart, item2, 2, item2.getPrice());
        entityManager.persist(detail1);
        entityManager.persist(detail2);
        entityManager.flush();

        // Act
        cartDetailRepository.deleteAllByCartId(cart.getId());
        entityManager.flush();

        // Assert
        assertEquals(0, cartDetailRepository.count());
    }

    @Test
    void sumTotalCostInCart_shouldReturnCorrectSum() {
        // Arrange
        Cart cart = new Cart("session1");
        Item item1 = new Item(1L, "Item1", "Desc1", "img1.jpg", new BigDecimal("10.00"));
        Item item2 = new Item(2L, "Item2", "Desc2", "img2.jpg", new BigDecimal("20.00"));

        entityManager.persist(cart);
        entityManager.persist(item1);
        entityManager.persist(item2);

        CartDetail detail1 = new CartDetail(cart, item1, 2, item1.getPrice()); // 10 * 2 = 20
        CartDetail detail2 = new CartDetail(cart, item2, 1, item2.getPrice()); // 20 * 1 = 20
        entityManager.persist(detail1);
        entityManager.persist(detail2);
        entityManager.flush();

        // Act
        Optional<BigDecimal> result = cartDetailRepository.sumTotalCostInCart(cart.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("40.00"), result.get());
    }

    @Test
    void sumTotalCostInCart_shouldReturnEmptyForEmptyCart() {
        // Arrange
        Cart cart = new Cart("empty-cart");
        entityManager.persist(cart);
        entityManager.flush();

        // Act
        Optional<BigDecimal> result = cartDetailRepository.sumTotalCostInCart(cart.getId());

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void shouldPersistCartDetailWithCompositeKey() {
        // Arrange
        Cart cart = new Cart("session1");
        Item item = new Item(1L, "Item1", "Desc1", "img1.jpg", new BigDecimal("10.00"));
        entityManager.persist(cart);
        entityManager.persist(item);

        CartDetail detail = new CartDetail(cart, item, 1, item.getPrice());

        // Act
        CartDetail savedDetail = cartDetailRepository.save(detail);
        entityManager.flush();

        // Assert
        assertNotNull(savedDetail);
        assertEquals(cart.getId(), savedDetail.getId().getCartId());
        assertEquals(item.getId(), savedDetail.getId().getItemId());
    }

    @Test
    void shouldNotAllowDuplicateCompositeKeys() {
        // Arrange
        Cart cart = new Cart("session1");
        Item item = new Item(1L, "Item1", "Desc1", "img1.jpg", new BigDecimal("10.00"));
        entityManager.persist(cart);
        entityManager.persist(item);

        CartDetail detail1 = new CartDetail(cart, item, 1, item.getPrice());
        cartDetailRepository.save(detail1);
        entityManager.flush();

        // Act & Assert
        CartDetail detail2 = new CartDetail(cart, item, 2, item.getPrice());
        assertThrows(Exception.class, () -> {
            cartDetailRepository.saveAndFlush(detail2);
        });
    }

    @Test
    void shouldNotAllowNullCartOrItem() {
        // Arrange
        Cart cart = new Cart("session1");
        Item item = new Item(1L, "Item1", "Desc1", "img1.jpg", new BigDecimal("10.00"));
        entityManager.persist(cart);
        entityManager.persist(item);

        // Act & Assert for null cart
        CartDetail detailWithNullCart = new CartDetail(null, item, 1, item.getPrice());
        assertThrows(Exception.class, () -> {
            cartDetailRepository.saveAndFlush(detailWithNullCart);
        });

        // Act & Assert for null item
        CartDetail detailWithNullItem = new CartDetail(cart, null, 1, item.getPrice());
        assertThrows(Exception.class, () -> {
            cartDetailRepository.saveAndFlush(detailWithNullItem);
        });
    }
}