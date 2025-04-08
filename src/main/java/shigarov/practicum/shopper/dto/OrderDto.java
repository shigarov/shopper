package shigarov.practicum.shopper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.domain.Order;
import shigarov.practicum.shopper.domain.OrderDetail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
public class OrderDto {
    private Long id;
    private List<ItemDto> items;

    private OrderDto() {}

    public static OrderDto of(@NonNull Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.id = order.getId();

        Set<OrderDetail> orderDetails = order.getDetails();
        orderDto.items = new ArrayList<>(orderDetails.size());

        for (OrderDetail orderDetail : orderDetails) {
            Item item = orderDetail.getItem();
//            Long id = item.getId();
//            String title = item.getTitle();
//            String description = item.getDescription();
//            String imgPath = item.getImgPath();
            Integer quantity = orderDetail.getQuantity();
            BigDecimal price = orderDetail.getPrice();
            ItemDto itemDto = ItemDto.of(item, quantity, price);
            orderDto.items.add(itemDto);
        }

        return orderDto;
    }
}
