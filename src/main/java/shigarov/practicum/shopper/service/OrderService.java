package shigarov.practicum.shopper.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import shigarov.practicum.shopper.domain.*;
import shigarov.practicum.shopper.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order buy(@NonNull Cart cart) {
        Order order = new Order();
        Set<OrderDetail> orderDetails = order.getDetails();
        Collection<CartDetail> cartDetails = cart.getDetails().values();

        for (CartDetail cartDetail : cartDetails) {
            Item item = cartDetail.getItem();
            Integer quantity = cartDetail.getQuantity();
            BigDecimal price = item.getPrice();

            OrderDetail orderDetail = new OrderDetail(order, item, quantity, price);
            orderDetails.add(orderDetail);
        }

        Order savedOrder = orderRepository.save(order);

        return savedOrder;
    }

    public List<Order> getAllOrders(@NonNull Cart cart) {
        return orderRepository.findByCartId(cart.getId());
    }

    public Optional<Order> getOrder(@NonNull Long id) {
        return orderRepository.findById(id);
    }
}
