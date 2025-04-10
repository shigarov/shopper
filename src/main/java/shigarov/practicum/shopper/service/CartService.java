package shigarov.practicum.shopper.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import shigarov.practicum.shopper.types.ActionType;
import shigarov.practicum.shopper.domain.Cart;
import shigarov.practicum.shopper.domain.CartDetail;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.repository.CartDetailRepository;
import shigarov.practicum.shopper.repository.CartRepository;

import java.math.BigDecimal;
import java.util.*;

import static shigarov.practicum.shopper.types.ActionType.*;

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
                    quantity ++;
                    cartDetail.setQuantity(quantity);
                }

                case MINUS -> {
                    Integer quantity = cartDetail.getQuantity();
                    quantity --;
                    if (quantity > 0) {
                        cartDetail.setQuantity(quantity);
                    } else {
                        cart.getDetails().remove(item);
                    }
                }

                case DELETE -> {
                    cart.getDetails().remove(item);
                }
            }
            cartRepository.save(cart);
        } else {
            if (action == PLUS) {
                Integer quantity = 1;
                BigDecimal price = item.getPrice();
                CartDetail cartDetail = new CartDetail(cart, item, quantity, price);
                cart.getDetails().put(item, cartDetail);
                cartRepository.save(cart);
            }
        }
    }

    public Optional<Cart> getCart(@NonNull Long cartId) {
        return cartRepository.findById(cartId);
    }

    public BigDecimal getCartTotalCost(@NonNull Cart cart) {
        return cartDetailRepository.sumTotalCostInCart(cart.getId()).orElse(BigDecimal.ZERO);
    }
}
