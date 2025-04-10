package shigarov.practicum.shopper.dto;

import org.springframework.lang.NonNull;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.domain.Order;
import shigarov.practicum.shopper.domain.OrderDetail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OrderDtoFactory {
    private ItemDtoFactory itemDtoFactory;

    public OrderDtoFactory(@NonNull ItemDtoFactory itemDtoFactory) {
        this.itemDtoFactory = itemDtoFactory;
    }

    public OrderDto of(@NonNull Order order, BigDecimal totalCost) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());

        Collection<OrderDetail> orderDetails = order.getDetails().values();
        List<ItemDto> items = new ArrayList<>(orderDetails.size());

        for (OrderDetail orderDetail : orderDetails) {
            Item item = orderDetail.getItem();
            Integer quantity = orderDetail.getQuantity();
            BigDecimal price = orderDetail.getPrice();
            ItemDto itemDto = itemDtoFactory.of(item, quantity, price);
            items.add(itemDto);
        }

        orderDto.setItems(items);

        // Подсчет итоговой суммы заказа
        orderDto.setTotalCost(totalCost);

        return orderDto;
    }
}
