package shigarov.practicum.shopper.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.repository.ItemRepository;
import shigarov.practicum.shopper.service.ItemService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemService itemService;

    @Test
    void getItems_shouldReturnPageOfItems() {
        // Подготовка тестовых данных
        Item testItem = new Item(1L, "title1", "desc1", "image1.jpg", BigDecimal.valueOf(1));
        Page<Item> expectedPage = new PageImpl<>(Collections.singletonList(testItem));

        when(itemRepository.findAllBySearchTerm(anyString(), any(Pageable.class))).thenReturn(expectedPage);

        // Тестируемое действие
        Page<Item> result = itemService.getItems("test", Pageable.unpaged());

        // Проверка результатов
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testItem, result.getContent().get(0));
    }

    @Test
    void getItems_withNullSearchTerm_shouldReturnPageOfItems() {
        // Подготовка тестовых данных
        Item testItem = new Item(1L, "title1", "desc1", "image1.jpg", BigDecimal.valueOf(1));
        Page<Item> expectedPage = new PageImpl<>(Collections.singletonList(testItem));
        //
        when(itemRepository.findAllBySearchTerm(nullable(String.class), any(Pageable.class))).thenReturn(expectedPage);

        // Тестируемое действие
        Page<Item> result = itemService.getItems(null, Pageable.unpaged());

        // Проверка результатов
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getItem_shouldReturnItemWhenExists() {
        // Подготовка тестовых данных
        Long itemId = 1L;
        Item expectedItem = new Item(1L, "title1", "desc1", "image1.jpg", BigDecimal.valueOf(1));

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(expectedItem));

        // Тестируемое действие
        Optional<Item> result = itemService.getItem(itemId);

        // Проверка результатов
        assertTrue(result.isPresent());
        assertEquals(expectedItem, result.get());
    }

    @Test
    void getItem_shouldReturnEmptyWhenNotExists() {
        // Подготовка тестовых данных
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Тестируемое действие
        Optional<Item> result = itemService.getItem(anyLong());

        // Проверка результатов
        assertTrue(result.isEmpty());
    }
}
