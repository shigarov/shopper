package shigarov.practicum.shopper.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.service.AdminService;
import shigarov.practicum.shopper.storage.StorageService;

import java.math.BigDecimal;

@Controller
public class AdminController {
    private final AdminService adminService;
    private final StorageService storageService;

    public AdminController(AdminService adminService, StorageService storageService) {
        this.adminService = adminService;
        this.storageService = storageService;
    }

    @PostMapping("/admin/items/add")
    public ResponseEntity<String> addItem(
            @RequestParam(name = "title") String title,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(name = "price") BigDecimal price
    ) {
        Item item = new Item();
        item.setTitle(title);
        item.setDescription(description);

        // Обработка загрузки файла
        if (imageFile != null && !imageFile.isEmpty()) {
            // Устанавливаем имя файла
            String fileName = imageFile.getOriginalFilename();
            item.setImgPath(fileName);
        }

        item.setPrice(price);

        // Сохраняем товар
        final Item savedItem = adminService.saveItem(item);

        if (savedItem != null) {
            final Long itemId = savedItem.getId();
            // Сохраняем изображение
            if (imageFile != null && !imageFile.isEmpty())
                storageService.store(itemId.toString(), imageFile);
        }

        Long itemId = savedItem.getId();
        String itemTitle = savedItem.getTitle();
        String message = String.format("Item has been added successfully: id=%d, title=\"%s\"", itemId, itemTitle);

        return ResponseEntity.ok(message);
    }

}
