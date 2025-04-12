package shigarov.practicum.shopper.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shigarov.practicum.shopper.domain.Cart;
import shigarov.practicum.shopper.domain.CartDetail;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.repository.CartDetailRepository;
import shigarov.practicum.shopper.repository.CartRepository;
import shigarov.practicum.shopper.types.ActionType;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartDetailRepository cartDetailRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void save_shouldCallRepositorySave() {
        // Arrange
        Cart cart = new Cart("1");
        when(cartRepository.save(cart)).thenReturn(cart);

        // Act
        Cart result = cartService.save(cart);

        // Assert
        assertNotNull(result);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void clear_shouldDeleteAllCartDetails() {
        // Arrange
        Cart cart = new Cart(1L, "1");

        // Act
        cartService.clear(cart);

        // Assert
        verify(cartDetailRepository, times(1)).deleteAllByCartId(1L);
    }

    @Test
    void updateCart_shouldIncreaseQuantityWhenActionIsPlusAndDetailExists() {
        // Arrange
        Cart cart = new Cart(1L, "1");
        Item item = new Item(1L, "title1", "desc1", "img1.jpg", BigDecimal.TEN);
        CartDetail cartDetail = new CartDetail(cart, item, 1, BigDecimal.TEN);
        cart.getDetails().put(item, cartDetail);

        when(cartRepository.save(cart)).thenReturn(cart);

        // Act
        cartService.updateCart(cart, item, ActionType.PLUS);

        // Assert
        assertEquals(2, cartDetail.getQuantity());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void updateCart_shouldDecreaseQuantityWhenActionIsMinusAndQuantityRemainsPositive() {
        // Arrange
        Cart cart = new Cart(1L, "1");
        Item item = new Item(1L, "title1", "desc1", "img1.jpg", BigDecimal.TEN);
        CartDetail cartDetail = new CartDetail(cart, item, 2, BigDecimal.TEN);
        cart.getDetails().put(item, cartDetail);

        when(cartRepository.save(cart)).thenReturn(cart);

        // Act
        cartService.updateCart(cart, item, ActionType.MINUS);

        // Assert
        assertEquals(1, cartDetail.getQuantity());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void updateCart_shouldRemoveDetailWhenActionIsMinusAndQuantityBecomesZero() {
        // Arrange
        Cart cart = new Cart(1L, "1");
        Item item = new Item(1L, "title1", "desc1", "img1.jpg", BigDecimal.TEN);
        CartDetail cartDetail = new CartDetail(cart, item, 1, BigDecimal.TEN);
        cart.getDetails().put(item, cartDetail);

        when(cartRepository.save(cart)).thenReturn(cart);

        // Act
        cartService.updateCart(cart, item, ActionType.MINUS);

        // Assert
        assertFalse(cart.getDetails().containsKey(item));
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void updateCart_shouldRemoveDetailWhenActionIsDelete() {
        // Arrange
        Cart cart = new Cart(1L, "1");
        Item item = new Item(1L, "title1", "desc1", "img1.jpg", BigDecimal.TEN);
        CartDetail cartDetail = new CartDetail(cart, item, 2, BigDecimal.TEN);
        cart.getDetails().put(item, cartDetail);

        when(cartRepository.save(cart)).thenReturn(cart);

        // Act
        cartService.updateCart(cart, item, ActionType.DELETE);

        // Assert
        assertFalse(cart.getDetails().containsKey(item));
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void updateCart_shouldAddNewDetailWhenActionIsPlusAndDetailNotExists() {
        // Arrange
        Cart cart = new Cart(1L, "1");
        Item item = new Item(1L, "title1", "desc1", "img1.jpg", BigDecimal.TEN);

        when(cartRepository.save(cart)).thenReturn(cart);

        // Act
        cartService.updateCart(cart, item, ActionType.PLUS);

        // Assert
        assertTrue(cart.getDetails().containsKey(item));
        assertEquals(1, cart.getDetails().get(item).getQuantity());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void getOrCreateCartBySessionId_shouldReturnExistingCart() {
        // Arrange
        String sessionId = "1";
        Cart existingCart = new Cart(1L, sessionId);
        when(cartRepository.findBySessionId(sessionId)).thenReturn(Optional.of(existingCart));

        // Act
        Cart result = cartService.getOrCreateCartBySessionId(sessionId);

        // Assert
        assertEquals(existingCart, result);
        verify(cartRepository, times(1)).findBySessionId(sessionId);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void getOrCreateCartBySessionId_shouldCreateNewCartWhenNotExists() {
        // Arrange
        String sessionId = "1";
        Cart newCart = new Cart(sessionId);
        when(cartRepository.findBySessionId(sessionId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);

        // Act
        Cart result = cartService.getOrCreateCartBySessionId(sessionId);

        // Assert
        assertNotNull(result);
        assertEquals(sessionId, result.getSessionId());
        verify(cartRepository, times(1)).findBySessionId(sessionId);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void saveCart_shouldCallRepositorySave() {
        // Arrange
        Cart cart = new Cart("1");
        when(cartRepository.save(cart)).thenReturn(cart);

        // Act
        Cart result = cartService.saveCart(cart);

        // Assert
        assertNotNull(result);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void getCartTotalCost_shouldReturnSumFromRepository() {
        // Arrange
        Long cartId = 1L;
        Cart cart = new Cart(cartId, "1");
        BigDecimal expectedSum = new BigDecimal("100.00");
        when(cartDetailRepository.sumTotalCostInCart(cartId)).thenReturn(Optional.of(expectedSum));

        // Act
        BigDecimal result = cartService.getCartTotalCost(cart);

        // Assert
        assertEquals(expectedSum, result);
        verify(cartDetailRepository, times(1)).sumTotalCostInCart(cartId);
    }

    @Test
    void getCartTotalCost_shouldReturnZeroWhenNoDetails() {
        // Arrange
        Long cartId = 1L;
        Cart cart = new Cart(cartId, "1");
        when(cartDetailRepository.sumTotalCostInCart(cartId)).thenReturn(Optional.empty());

        // Act
        BigDecimal result = cartService.getCartTotalCost(cart);

        // Assert
        assertEquals(BigDecimal.ZERO, result);
        verify(cartDetailRepository, times(1)).sumTotalCostInCart(cartId);
    }
}
