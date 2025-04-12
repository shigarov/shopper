package shigarov.practicum.shopper.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.repository.ItemRepository;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    void saveItem_shouldCallRepositorySaveAndReturnSavedItem() {
        // Arrange
        Item inputItem = new Item(null, "title1", "desc1", "img1.jpg", BigDecimal.ONE);
        Item savedItem = new Item(1L, "title1", "desc1", "img1.jpg", BigDecimal.ONE);

        when(itemRepository.save(inputItem)).thenReturn(savedItem);

        // Act
        Item result = adminService.saveItem(inputItem);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("title1", result.getTitle());
        assertEquals("desc1", result.getDescription());
        assertEquals("img1.jpg", result.getImgPath());
        assertEquals(BigDecimal.ONE, result.getPrice());

        verify(itemRepository, times(1)).save(inputItem);
    }

    @Test
    void saveItem_shouldHandleNullFieldsAccordingToEntityConstraints() {
        // Arrange
        Item inputItem = new Item(null, null, null, null, null);

        // Для тестирования валидации можно использовать исключение
        when(itemRepository.save(inputItem)).thenThrow(new RuntimeException("Validation failed"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> adminService.saveItem(inputItem));

        verify(itemRepository, times(1)).save(inputItem);
    }

    @Test
    void saveItem_shouldUpdateExistingItem() {
        // Arrange
        Item existingItem = new Item(1L, "title1", "desc1", "img1.jpg", BigDecimal.ONE);
        Item updatedItem = new Item(1L, "title2", "desc2", "img2.jpg", BigDecimal.TWO);

        when(itemRepository.save(existingItem)).thenReturn(updatedItem);

        // Act
        Item result = adminService.saveItem(existingItem);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("title2", result.getTitle());
        assertEquals("desc2", result.getDescription());
        assertEquals("img2.jpg", result.getImgPath());
        assertEquals(BigDecimal.TWO, result.getPrice());

        verify(itemRepository, times(1)).save(existingItem);
    }
}
