package shigarov.practicum.shopper.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import jakarta.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.hasSize;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.springframework.ui.Model;
import shigarov.practicum.shopper.domain.Cart;
import shigarov.practicum.shopper.domain.CartDetail;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.dto.ItemDto;
import shigarov.practicum.shopper.service.CartService;
import shigarov.practicum.shopper.service.ItemService;

import java.math.BigDecimal;
import java.util.*;

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

    @MockitoBean
    private Model model;

    @Test
    void showItems_shouldReturnMainViewWithItems() throws Exception {
        // Подготовка тестовых данных
        String sessionId = "1";

        Item item1 = new Item("Item 1", "Desc 1", null, BigDecimal.ONE);
        Item item2 = new Item("Item 2", "Desc 2", null, BigDecimal.TWO);
        List<Item> testItems = List.of(item1, item2);
        Page<Item> page = new PageImpl<>(testItems);

        Cart cart = new Cart(1L, sessionId);
        CartDetail cartDetail1 = new CartDetail(cart, item1, 1, item1.getPrice());
        CartDetail cartDetail2 = new CartDetail(cart, item2, 2, item2.getPrice());

        cart.getDetails().put(item1, cartDetail1);
        cart.getDetails().put(item2, cartDetail2);

        when(itemService.getItems(anyString(), any(Pageable.class))).thenReturn(page);
        when(session.getId()).thenReturn(sessionId);
        when(cartService.getOrCreateCartBySessionId(sessionId)).thenReturn(cart);

        MockHttpSession session = new MockHttpSession(null, sessionId);

        // Выполнение и проверка
        mockMvc.perform(get("/main/items")
                        .session(session)
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
        String sessionId = "1";
        Item testItem = new Item(1L, "title1", "desc1", "image1.jpg", BigDecimal.valueOf(1.0));

        when(itemService.getItem(1L)).thenReturn(Optional.of(testItem));
        when(session.getId()).thenReturn(sessionId);
        when(cartService.getOrCreateCartBySessionId(anyString())).thenReturn(new Cart(1L, sessionId));

        MockHttpSession session = new MockHttpSession(null, sessionId);

        // Выполнение и проверка
        mockMvc.perform(get("/items/1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"));
    }

    @Test
    public void showItem_shouldThrowExceptionWhenItemNotExists() throws Exception {
        // Arrange
        Long nonExistentItemId = 0L;
        when(itemService.getItem(nonExistentItemId)).thenReturn(Optional.empty());

        MockHttpSession session = new MockHttpSession(null, "1");

        // Act & Assert
        mockMvc.perform(get("/items/0")
                        .session(session))
                .andExpect(status().isNotFound());

        // Verify
        verify(itemService).getItem(nonExistentItemId);
        verifyNoInteractions(cartService); // cartService не должен использоваться, если товар не найден
    }

    @Test
    public void showItems_shouldSplitItemsIntoRowsCorrectly() throws Exception {
        // Arrange
        String sessionId = "1";

        List<Item> testItems = List.of(
                new Item("Item 1", "Desc 1", null, BigDecimal.valueOf(1)),
                new Item("Item 2", "Desc 2", null, BigDecimal.valueOf(2)),
                new Item("Item 3", "Desc 3", null, BigDecimal.valueOf(3)),
                new Item("Item 4", "Desc 4", null, BigDecimal.valueOf(4)),
                new Item("Item 5", "Desc 5", null, BigDecimal.valueOf(5))
        );

        Page<Item> page = new PageImpl<>(testItems);
        when(itemService.getItems(anyString(), any(Pageable.class))).thenReturn(page);
        when(session.getId()).thenReturn(sessionId);
        when(cartService.getOrCreateCartBySessionId(sessionId)).thenReturn(new Cart(1L, sessionId));

        MockHttpSession session = new MockHttpSession(null, sessionId);

        // Act
        mockMvc.perform(get("/main/items")
                        .session(session)
                        .param("pageSize", "10")
                        .param("pageNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attribute("items", instanceOf(List.class)))
                .andExpect(model().attribute("items", hasSize(2))) // Ожидаем 2 строки (по 2 товара в каждой)
                .andDo(result -> {
                    // Детальная проверка структуры данных
                    List<List<ItemDto>> itemsInRows = (List<List<ItemDto>>) result.getModelAndView()
                            .getModel().get("items");

                    assertEquals(2, itemsInRows.size()); // Проверяем количество строк

                    // Проверяем первую строку (2 товара)
                    assertEquals(3, itemsInRows.get(0).size());
                    assertEquals("Item 1", itemsInRows.get(0).get(0).getTitle());
                    assertEquals("Item 2", itemsInRows.get(0).get(1).getTitle());
                    assertEquals("Item 3", itemsInRows.get(0).get(2).getTitle());

                    // Проверяем вторую строку (2 товара)
                    assertEquals(2, itemsInRows.get(1).size());
                    assertEquals("Item 4", itemsInRows.get(1).get(0).getTitle());
                    assertEquals("Item 5", itemsInRows.get(1).get(1).getTitle());
                }
                );

        // Проверка вызовов сервисов
        verify(itemService).getItems(eq(""), any(Pageable.class));
        verify(cartService).getOrCreateCartBySessionId(sessionId);
    }

}
