package shigarov.practicum.shopper.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import shigarov.practicum.shopper.domain.Cart;
import shigarov.practicum.shopper.domain.CartDetail;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.repository.CartDetailRepository;
import shigarov.practicum.shopper.repository.CartRepository;

import java.math.BigDecimal;
import java.util.*;

import static shigarov.practicum.shopper.service.ActionType.*;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartDetailRepository cartDetailRepository;

    public CartService(
            CartRepository cartRepository,
            CartDetailRepository cartDetailRepository//,
    ) {
        this.cartRepository = cartRepository;
        this.cartDetailRepository = cartDetailRepository;
    }

    public Cart save(@NonNull Cart cart) {
        return cartRepository.save(cart);
    }

    public void clear(@NonNull Cart cart) {
        cartDetailRepository.deleteAllByCartId(cart.getId());
    }

    public void updateCart(
            @NonNull Cart cart,
            @NonNull Item item,
            @NonNull ActionType action
    ) {
        Optional<CartDetail> cartDetailOptional = cart.getCartDetail(item);

        if (cartDetailOptional.isPresent()) {
            CartDetail cartDetail = cartDetailOptional.get();

            switch (action) {
                case PLUS -> {
                    Integer quantity = cartDetail.getQuantity();
                    quantity++;
                    cartDetail.setQuantity(quantity);
                }

                case MINUS -> {
                    Integer quantity = cartDetail.getQuantity();
                    quantity--;
                    if (quantity > 0) {
                        cartDetail.setQuantity(quantity);
                    } else {
                        Long itemId = cartDetail.getId().getItemId();
                        cart.getDetails().remove(itemId);
                    }
                }

                case DELETE -> {
                    Long itemId = cartDetail.getId().getItemId();
                    cart.getDetails().remove(itemId);
                }
            }
            cartRepository.save(cart);
        } else {
            if (action == PLUS) {
                CartDetail cartDetail = new CartDetail(cart, item, 1);
                cart.getDetails().put(item, cartDetail);
                cartRepository.save(cart);
            }
        }
    }

    public Optional<Cart> getCart(@NonNull Long cartId) {
        return cartRepository.findById(cartId);
    }

    public BigDecimal calculateTotalCost(@NonNull Cart cart) {
        BigDecimal totalCost = BigDecimal.ZERO;
        Collection<CartDetail> cartDetails = cart.getDetails().values();

        for (CartDetail cartDetail : cartDetails) {
            BigDecimal quantity = BigDecimal.valueOf(cartDetail.getQuantity());
            BigDecimal price = cartDetail.getItem().getPrice();
            BigDecimal cost = price.multiply(quantity);
            totalCost.add(cost);
        }

        return totalCost;
    }
}
