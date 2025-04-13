package shigarov.practicum.shopper.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
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
@ActiveProfiles("test")
class CartDetailRepositoryTest {

    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @Transactional
    void deleteAllByCartId_shouldRemoveAllDetailsForCart() {
        // Arrange
        Cart cart = new Cart("1");
        entityManager.persist(cart);

        Item item1 = new Item("title1", "desc1", "img1.jpg", BigDecimal.ONE);
        Item item2 = new Item("title2", "desc2", "img2.jpg", BigDecimal.TWO);
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
    @Transactional
    void sumTotalCostInCart_shouldReturnCorrectSum() {
        // Arrange
        Cart cart = new Cart("session1");
        entityManager.persist(cart);

        Item item1 = new Item("title1", "desc1", "img1.jpg", BigDecimal.ONE);
        Item item2 = new Item("title2", "desc2", "img2.jpg", BigDecimal.TWO);
        entityManager.persist(item1);
        entityManager.persist(item2);

        CartDetail detail1 = new CartDetail(cart, item1, 2, item1.getPrice()); // 1 * 2 = 2
        CartDetail detail2 = new CartDetail(cart, item2, 1, item2.getPrice()); // 2 * 1 = 2
        entityManager.persist(detail1);
        entityManager.persist(detail2);
        entityManager.flush();

        // Act
        Optional<BigDecimal> result = cartDetailRepository.sumTotalCostInCart(cart.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("4.00"), result.get());
    }

    @Test
    @Transactional
    void sumTotalCostInCart_shouldReturnEmptyForEmptyCart() {
        // Arrange
        Cart cart = new Cart("1");
        entityManager.persist(cart);
        entityManager.flush();

        // Act
        Optional<BigDecimal> result = cartDetailRepository.sumTotalCostInCart(cart.getId());

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    @Transactional
    void shouldPersistCartDetailWithCompositeKey() {
        // Arrange
        Cart cart = new Cart("1");
        entityManager.persist(cart);

        Item item = new Item("title1", "desc1", "img1.jpg", BigDecimal.ONE);
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
}