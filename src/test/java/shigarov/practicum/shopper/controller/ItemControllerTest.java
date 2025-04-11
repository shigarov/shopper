package shigarov.practicum.shopper.controller;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import shigarov.practicum.shopper.domain.Cart;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.service.CartService;
import shigarov.practicum.shopper.service.ItemService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private HttpSession session;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    void setUp() {
        // Инициализация контроллера с тестовыми значениями
        //itemController = new ItemController(itemService, cartService);
        // itemController.itemRowSize = 3; // тестовое значение для presentation.item-row-size
        // itemController.imagesDir = "images-test"; // тестовое значение для storage.images-dir
        // itemController.postConstruct(); // инициализация itemDtoFactory

        //mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @Test
    void showItems_shouldReturnMainViewWithItems() throws Exception {
        // Подготовка тестовых данных
        List<Item> testItems = generateTestItems(2);
        Page<Item> page = new PageImpl<>(testItems);

        when(itemService.getItems(anyString(), any(Pageable.class))).thenReturn(page);
        when(session.getId()).thenReturn("1");
        when(cartService.getOrCreateCartBySessionId(anyString())).thenReturn(new Cart(1L, "1"));

        // Выполнение и проверка
        mockMvc.perform(get("/main/items")
                        .sessionAttr("session", session)
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
    void showItem_shouldReturnItemViewWhenItemExists() throws Exception {
        // Подготовка тестовых данных
        Item testItem = new Item(1L, "title1", "desc1", "image1.jpg", BigDecimal.valueOf(1.0));

        when(itemService.getItem(1L)).thenReturn(Optional.of(testItem));
        when(session.getId()).thenReturn("1");
        when(cartService.getOrCreateCartBySessionId(anyString())).thenReturn(new Cart(1L, "1"));

        // Выполнение и проверка
        mockMvc.perform(get("/items/1")
                        .sessionAttr("session", session))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"));
    }

    @Test
    void showItem_shouldThrowExceptionWhenItemNotExists() throws Exception {
        // Подготовка тестовых данных
        when(itemService.getItem(3L)).thenReturn(Optional.empty());

        // Выполнение и проверка
//        mockMvc.perform(get("/items/3"))
//                .andExpect(status().isNotFound());
    }

    @Test
    void showItems_shouldSplitItemsIntoRowsCorrectly() throws Exception {
        // Подготовка тестовых данных (6 items, rowSize = 3 → 2 rows)
        List<Item> testItems = generateTestItems(6);
        Page<Item> page = new PageImpl<>(testItems);

        when(itemService.getItems(anyString(), any(Pageable.class))).thenReturn(page);
        when(session.getId()).thenReturn("1");
        when(cartService.getOrCreateCartBySessionId(anyString())).thenReturn(new Cart());

        // Выполнение и проверка
//        mockMvc.perform(get("/main/items"))
//                .andExpect(status().isOk())
//                .andExpect(model().attribute("items", org.hamcrest.Matchers.hasSize(2)));
    }

    private List<Item> generateTestItems(int count) {
        List<Item> items = new ArrayList<>(count);

        for (long i = 1L; i <= count; i++) {
            items.add(new Item(
                    i,
                    "title " + i,
                    "description " + i,
                    "image" + i + ".jpg",
                    BigDecimal.valueOf(i)
            ));
        }

        return items;
    }
}
