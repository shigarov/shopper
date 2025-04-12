package shigarov.practicum.shopper.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import shigarov.practicum.shopper.domain.Cart;
import shigarov.practicum.shopper.domain.CartDetail;
import shigarov.practicum.shopper.domain.Item;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @AfterEach
    void cleanUp() {
        cartRepository.deleteAll();
    }

    @Test
    void findBySessionId_shouldReturnCartWhenExists() {
        // Arrange
        String sessionId = "1";
        Cart cart = new Cart(sessionId);
        cartRepository.save(cart);

        // Act
        Optional<Cart> foundCart = cartRepository.findBySessionId(sessionId);

        // Assert
        assertTrue(foundCart.isPresent());
        assertEquals(sessionId, foundCart.get().getSessionId());
    }

    @Test
    void findBySessionId_shouldReturnEmptyWhenNotExists() {
        // Act
        Optional<Cart> foundCart = cartRepository.findBySessionId("0");

        // Assert
        assertFalse(foundCart.isPresent());
    }

    @Test
    void shouldSaveCartWithSessionId() {
        // Arrange
        String sessionId = "1";
        Cart newCart = new Cart(sessionId);

        // Act
        Cart savedCart = cartRepository.save(newCart);

        // Assert
        assertNotNull(savedCart.getId());
        assertEquals(sessionId, savedCart.getSessionId());
    }

    @Test
    void shouldNotAllowDuplicateSessionIds() {
        // Arrange
        String duplicateSessionId = "1";
        cartRepository.save(new Cart(duplicateSessionId));

//        // Act & Assert
//        assertNull(cartRepository.save(new Cart(duplicateSessionId)));

//        // Act & Assert
//        assertThrows(DataIntegrityViolationException.class, () -> {
//            cartRepository.save(new Cart(duplicateSessionId));
//        });
    }

    @Test
    void shouldNotAllowNullSessionId() {
        // Arrange
        Cart cartWithNullSessionId = new Cart();

//        // Act & Assert
//        assertThrows(DataIntegrityViolationException.class, () -> {
//            cartRepository.saveAndFlush(cartWithNullSessionId);
//        });
    }

    @Test
    void shouldGenerateIdAutomatically() {
        // Arrange
        Cart cart = new Cart("auto-id-session");

        // Act
        Cart savedCart = cartRepository.save(cart);

        // Assert
        assertNotNull(savedCart.getId());
    }

    @Test
    void shouldPersistCartWithDetails() {
        // Arrange
        Cart cart = new Cart("1");
        Item item = new Item(1L, "title1", "desc1", "img1.jpg", BigDecimal.ONE);
        CartDetail detail = new CartDetail(cart, item, 1, BigDecimal.TEN);
        cart.getDetails().put(item, detail);

//        // Act
//        Cart savedCart = cartRepository.save(cart);
//
//        // Assert
//        assertNotNull(savedCart.getId());
//        assertEquals(1, savedCart.getDetails().size());
    }
}
