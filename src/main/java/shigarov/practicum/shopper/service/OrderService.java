package shigarov.practicum.shopper.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import shigarov.practicum.shopper.domain.Cart;
import shigarov.practicum.shopper.domain.Order;
import shigarov.practicum.shopper.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(@NonNull Cart cart) {
        Order order = Order.of(cart);
        Order savedOrder = orderRepository.save(order);

        return savedOrder;
    }

    public List<Order> getAllOrders(@NonNull Cart cart) {
        orderRepository.findByCartId(cart.getId());

        return null;
    }

    public Optional<Order> getOrder(@NonNull Long id) {
        return orderRepository.findById(id);
    }
}
