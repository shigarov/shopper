package shigarov.practicum.shopper.dto;

import org.springframework.lang.NonNull;

import shigarov.practicum.shopper.domain.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OrderDto {
    private Long id;
    private List<ItemDto> items;
    private BigDecimal totalCost;

    private OrderDto() {}

    public Long id() {
        return id;
    }

    public List<ItemDto> items() {
        return items;
    }

    public BigDecimal totalSum() {
        return totalCost;
    }

    public static OrderDto of(@NonNull Order order, BigDecimal totalCost) {
        OrderDto orderDto = new OrderDto();
        orderDto.id = order.getId();

        Collection<OrderDetail> orderDetails = order.getDetails().values();
        orderDto.items = new ArrayList<>(orderDetails.size());

        for (OrderDetail orderDetail : orderDetails) {
            Item item = orderDetail.getItem();
            Integer quantity = orderDetail.getQuantity();
            BigDecimal price = orderDetail.getPrice();
            ItemDto itemDto = ItemDto.of(item, quantity, price);
            orderDto.items.add(itemDto);
        }

        // Подсчет итоговой суммы заказа
        //orderDto.totalCost = order.totalCost();
        orderDto.totalCost = totalCost;

        return orderDto;
    }

}
