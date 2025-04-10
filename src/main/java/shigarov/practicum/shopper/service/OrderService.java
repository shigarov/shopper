package shigarov.practicum.shopper.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import shigarov.practicum.shopper.domain.*;
import shigarov.practicum.shopper.repository.OrderDetailRepository;
import shigarov.practicum.shopper.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public Order buy(@NonNull Cart cart) {
        Order order = new Order(cart);
        Order savedOrder = orderRepository.save(order);

        return savedOrder;
    }

    public List<Order> getAllOrders(@NonNull Cart cart) {
        return orderRepository.findByCartId(cart.getId());
    }

    public Optional<Order> getOrder(@NonNull Long id) {
        return orderRepository.findById(id);
    }

    public BigDecimal getOrderTotalCost(@NonNull Order order) {
        return orderDetailRepository.sumTotalCostInOrder(order.getId()).orElse(BigDecimal.ZERO);
    }
}
