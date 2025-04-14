package shigarov.practicum.shopper.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shigarov.practicum.shopper.domain.Cart;
import shigarov.practicum.shopper.domain.CartDetail;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.dto.ItemDtoFactory;
import shigarov.practicum.shopper.repository.CartRepository;
import shigarov.practicum.shopper.repository.ItemRepository;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CartControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CartRepository cartRepository;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public ItemDtoFactory itemDtoFactory() {
            return new ItemDtoFactory("images-test");
        }
    }

    private Item item1;
    private Item item2;
    private CartDetail cartDetail1;
    private CartDetail cartDetail2;
    private Cart cart;
    private String sessionId = "1";

    @Test
    @Transactional
    void showCart_shouldReturnCartViewWithItems() throws Exception {
        item1 = new Item("Item 1", "Desc 1", "img1.jpg", BigDecimal.ONE);
        item2 = new Item("Item 2", "Desc 2", "img2.jpg", BigDecimal.TWO);

        item1 = itemRepository.save(item1);
        item2 = itemRepository.save(item2);

        cart = new Cart(sessionId);
        cartRepository.save(cart);

        cartDetail1 = new CartDetail(cart, item1, 1, item1.getPrice());
        cartDetail2 = new CartDetail(cart, item2, 2, item2.getPrice());

        cart.getDetails().put(item1, cartDetail1);
        cartRepository.save(cart);
        cart.getDetails().put(item2, cartDetail2);
        cartRepository.save(cart);

        // Подготовка данных корзины
        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        // Выполнение и проверка
        mockMvc.perform(get("/cart/items").session(mockSession))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attribute("empty", false))
                .andExpect(model().attribute("items", hasSize(2)));
    }

    @Test
    @Transactional
    void showCart_shouldReturnEmptyCart() throws Exception {
        cart = new Cart(sessionId);
        cartRepository.save(cart);

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(get("/cart/items").session(mockSession))
                .andExpect(status().isOk())
                .andExpect(model().attribute("empty", true))
                .andExpect(model().attribute("items", empty()));
    }

    @Test
    @Transactional
    void updateCartByMainPage_shouldRedirectToMain() throws Exception {
        item1 = new Item("Item 1", "Desc 1", "img1.jpg", BigDecimal.ONE);
        item1 = itemRepository.save(item1);
        Long itemId = item1.getId();

        cart = new Cart(sessionId);
        cartRepository.save(cart);

        cartDetail1 = new CartDetail(cart, item1, 1, item1.getPrice());
        cart.getDetails().put(item1, cartDetail1);
        cartRepository.save(cart);

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(post("/main/items/" + itemId)
                        .session(mockSession)
                        .param("action", "PLUS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));
    }

    @Test
    @Transactional
    void updateCartByCartPage_shouldRedirectToCart() throws Exception {
        item1 = new Item("Item 1", "Desc 1", "img1.jpg", BigDecimal.ONE);
        item1 = itemRepository.save(item1);
        Long itemId = item1.getId();

        cart = new Cart(sessionId);
        cartRepository.save(cart);

        cartDetail1 = new CartDetail(cart, item1, 1, item1.getPrice());
        cart.getDetails().put(item1, cartDetail1);
        cartRepository.save(cart);

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(post("/cart/items/" + itemId)
                        .session(mockSession)
                        .param("action", "DELETE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items"));
    }

    @Test
    @Transactional
    void updateCartByItemPage_shouldRedirectToItemPage() throws Exception {
        item1 = new Item("Item 1", "Desc 1", "img1.jpg", BigDecimal.ONE);
        item1 = itemRepository.save(item1);
        Long itemId = item1.getId();

        cart = new Cart(sessionId);
        cartRepository.save(cart);

        cartDetail1 = new CartDetail(cart, item1, 1, item1.getPrice());
        cart.getDetails().put(item1, cartDetail1);
        cartRepository.save(cart);

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(post("/items/" + itemId)
                        .session(mockSession)
                        .param("action", "MINUS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/" + itemId));
    }

    @Test
    @Transactional
    void updateCart_shouldThrowExceptionWhenItemNotFound() throws Exception {
        cart = new Cart(sessionId);
        cartRepository.save(cart);

        MockHttpSession mockSession = new MockHttpSession(null, sessionId);

        mockMvc.perform(post("/cart/items/0")
                        .session(mockSession)
                        .param("action", "PLUS"))
                .andExpect(status().isNotFound());
    }
}
