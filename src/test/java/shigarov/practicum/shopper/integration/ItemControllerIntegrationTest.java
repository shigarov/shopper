package shigarov.practicum.shopper.integration;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.dto.ItemDtoFactory;
import shigarov.practicum.shopper.repository.CartRepository;
import shigarov.practicum.shopper.repository.ItemRepository;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ItemControllerIntegrationTest {
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

    @BeforeEach
    void setUp() {
        item1 = new Item("Item 1", "Desc 1", "img1.jpg", BigDecimal.ONE);
        item2 = new Item("Item 2", "Desc 2", "img2.jpg", BigDecimal.TWO);

        item1 = itemRepository.save(item1);
        item2 = itemRepository.save(item2);
    }

    @AfterEach
    void cleanUp() {
        itemRepository.delete(item1);
        itemRepository.delete(item2);
    }

    @Test
    public void showItems_ShouldReturnMainViewWithItems() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/main/items")
                        .param("search", "Item")
                        .param("sort", "NO")
                        .param("pageSize", "10")
                        .param("pageNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("search"))
                .andExpect(model().attributeExists("sort"))
                .andExpect(model().attributeExists("paging"));
    }

    @Test
    public void showItem_ShouldReturnItemView_WhenItemExists() throws Exception {
        Long itemId = itemRepository.findAll().getFirst().getId();
        // Act & Assert
        mockMvc.perform(get("/items/" + itemId))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"));
    }

    @Test
    public void showItem_ShouldReturnNotFound_WhenItemDoesNotExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/items/0"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void showItems_ShouldHandleEmptySearch() throws Exception {
        mockMvc.perform(get("/main/items"))
                .andExpect(status().isOk());
    }
}
