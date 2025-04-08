package shigarov.practicum.shopper.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import shigarov.practicum.shopper.domain.Cart;
import shigarov.practicum.shopper.domain.CartDetail;
import shigarov.practicum.shopper.domain.Item;
import shigarov.practicum.shopper.repository.CartRepository;
import shigarov.practicum.shopper.repository.ItemRepository;

import java.math.BigDecimal;
import java.util.*;

import static shigarov.practicum.shopper.service.ActionType.*;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    public CartService(CartRepository cartRepository, ItemRepository itemRepository) {
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
    }

    public void updateCart(
            @NonNull Long cartId,
            @NonNull Long itemId,
            @NonNull ActionType action
    ) {
        Optional<Cart> cartOptional = getCart(cartId);
        Cart cart = cartOptional.orElseThrow(() -> new NoSuchElementException("Invalid cart"));
        Optional<CartDetail> cartDetailOptional = findCartDetailByItem(cart, itemId);

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
                        cart.getDetails().remove(cartDetail);
                    }
                }

                case DELETE -> {
                    cart.getDetails().remove(cartDetail);
                }
            }
            cartRepository.save(cart);
        } else {
            if (action == PLUS) {
                Optional<Item> itemOptional = itemRepository.findById(itemId);
                Item item = itemOptional.orElseThrow();

                CartDetail cartDetail = new CartDetail(cart, item, 1);
                cart.getDetails().add(cartDetail);

                cartRepository.save(cart);
            }
        }
    }

    private Optional<CartDetail> findCartDetailByItem(@NonNull Cart cart, @NonNull Long itemId) {
        Set<CartDetail> cartDetails = cart.getDetails();

        for (CartDetail cartDetail : cartDetails) {
            Item item = cartDetail.getItem();
            if (item.getId() == itemId)
                return Optional.of(cartDetail);
        }

        return Optional.empty();
    }

    public Optional<Cart> getCart(@NonNull Long cartId) {
        return cartRepository.findById(cartId);
    }

    public BigDecimal calculateTotalCost(@NonNull Cart cart) {
        Set<CartDetail> cartDetails = cart.getDetails();
        BigDecimal totalCost = BigDecimal.ZERO;

        for (CartDetail cartDetail : cartDetails) {
            BigDecimal quantity = BigDecimal.valueOf(cartDetail.getQuantity());
            BigDecimal price = cartDetail.getItem().getPrice();
            BigDecimal cost = price.multiply(quantity);
            totalCost.add(cost);
        }

        return totalCost;
    }
}
