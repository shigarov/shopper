package shigarov.practicum.shopper.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import shigarov.practicum.shopper.domain.Cart;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.service.CartService;
import shigarov.practicum.shopper.service.ItemService;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import shigarov.practicum.shopper.domain.CartDetail;
import shigarov.practicum.shopper.types.ActionType;

@WebMvcTest(CartController.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private HttpSession session;

    private final String sessionId = "1";
    private Cart testCart;
    private Item testItem;

    @Test
    void showCart_shouldReturnCartViewWithItems() throws Exception {
        // Подготовка данных корзины
        testItem = new Item(1L, "Item", "Desc", "img.jpg", BigDecimal.ONE);
        testCart = new Cart(1L, sessionId);

        when(session.getId()).thenReturn(sessionId);
        when(cartService.getOrCreateCartBySessionId(sessionId)).thenReturn(testCart);

        CartDetail cartDetail = new CartDetail(testCart, testItem, 2, testItem.getPrice());
        testCart.getDetails().put(testItem, cartDetail);

        when(cartService.getCartTotalCost(testCart)).thenReturn(BigDecimal.valueOf(2));

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        // Выполнение и проверка
        mockMvc.perform(get("/cart/items").session(mockSession))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attribute("total", BigDecimal.valueOf(2)))
                .andExpect(model().attribute("empty", false))
                .andExpect(model().attribute("items", hasSize(1)));
    }

    @Test
    void showCart_shouldReturnEmptyCart() throws Exception {
        testCart = new Cart(1L, sessionId);

        when(session.getId()).thenReturn(sessionId);
        when(cartService.getOrCreateCartBySessionId(sessionId)).thenReturn(testCart);

        when(cartService.getCartTotalCost(testCart))
                .thenReturn(BigDecimal.ZERO);

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(get("/cart/items").session(mockSession))
                .andExpect(status().isOk())
                .andExpect(model().attribute("empty", true))
                .andExpect(model().attribute("items", empty()));
    }

    @Test
    void updateCartByMainPage_shouldRedirectToMain() throws Exception {
        testItem = new Item(1L, "Item", "Desc", "img.jpg", BigDecimal.ONE);
        testCart = new Cart(1L, sessionId);

        when(session.getId()).thenReturn(sessionId);
        when(cartService.getOrCreateCartBySessionId(sessionId)).thenReturn(testCart);

        when(itemService.getItem(1L)).thenReturn(Optional.of(testItem));

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(post("/main/items/1")
                        .session(mockSession)
                        .param("action", "PLUS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));

        verify(cartService).updateCart(eq(testCart), eq(testItem), eq(ActionType.PLUS));
    }

    @Test
    void updateCartByCartPage_shouldRedirectToCart() throws Exception {
        testItem = new Item(1L, "Item", "Desc", "img.jpg", BigDecimal.ONE);
        testCart = new Cart(1L, sessionId);

        when(session.getId()).thenReturn(sessionId);
        when(cartService.getOrCreateCartBySessionId(sessionId)).thenReturn(testCart);

        when(itemService.getItem(1L)).thenReturn(Optional.of(testItem));

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(post("/cart/items/1")
                        .session(mockSession)
                        .param("action", "DELETE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items"));

        verify(cartService).updateCart(eq(testCart), eq(testItem), eq(ActionType.DELETE));
    }

    @Test
    void updateCartByItemPage_shouldRedirectToItemPage() throws Exception {
        testItem = new Item(1L, "Item", "Desc", "img.jpg", BigDecimal.ONE);
        testCart = new Cart(1L, sessionId);

        when(session.getId()).thenReturn(sessionId);
        when(cartService.getOrCreateCartBySessionId(sessionId)).thenReturn(testCart);

        when(itemService.getItem(1L)).thenReturn(Optional.of(testItem));

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(post("/items/1")
                        .session(mockSession)
                        .param("action", "MINUS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/1"));

        verify(cartService).updateCart(eq(testCart), eq(testItem), eq(ActionType.MINUS));
    }

    @Test
    void updateCart_shouldThrowExceptionWhenItemNotFound() throws Exception {
        testCart = new Cart(1L, sessionId);

        when(session.getId()).thenReturn(sessionId);
        when(cartService.getOrCreateCartBySessionId(sessionId)).thenReturn(testCart);

        when(itemService.getItem(0L)).thenReturn(Optional.empty());

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(post("/cart/items/0")
                        .session(mockSession)
                        .param("action", "PLUS"))
                .andExpect(status().isNotFound());
    }
}
