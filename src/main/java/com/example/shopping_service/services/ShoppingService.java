package com.example.shopping_service.services;

import com.example.shopping_service.client.ProductServiceClient;
import com.example.shopping_service.client.UserServiceClient;
import com.example.shopping_service.exceptions.*;
import com.example.shopping_service.models.dtos.ProductDTO;
import com.example.shopping_service.models.dtos.SaleDTO;
import com.example.shopping_service.models.dtos.UserDTO;
import com.example.shopping_service.models.entities.Cart;
import com.example.shopping_service.models.entities.CartItem;
import com.example.shopping_service.repositories.CartRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@Service
public class ShoppingService {

    @Autowired
    private ProductServiceClient productServiceClient;

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private CartRepository cartRepository;

    public Cart addToCart(Long idUser, Long idProduct, Integer quantity)
            throws ProductNotFoundException, NoStockException, UserNotFoundException {

        log.info("productServiceClient.findProductById");
        ProductDTO productDTO = productServiceClient.findProductById(idProduct);
        if (productDTO == null) {
            throw new ProductNotFoundException("El producto no existe en base de datos");
        }

        Cart cart = cartRepository.findByIdUser(idUser);

        Integer stock = productDTO.getStock();
        log.info("stock={}", stock);
        log.info("quantity={}", quantity);
        if (stock - quantity >= 0) { // podemos añadir al carrito
            log.info("userServiceClient.getUserById");
            UserDTO userDTO = userServiceClient.getUserById(idUser);

            if (userDTO == null) {
                throw new UserNotFoundException("Usuario no encontrado");
            }

            if (cart == null) {// inicializamos cart
                log.info("inicializamos cart");
                cart = Cart.builder()
                        .idUser(userDTO.getId())
                        .email(userDTO.getEmail())
                        .total(0.0)
                        .cartItems(new ArrayList<>())
                        .build();
            }

            CartItem cartItem = cart.getCartItems().stream()
                    .filter(item -> item.getIdProduct().equals(idProduct))
                    .findFirst()
                    .orElse(null);

            if (cartItem != null) {// si tenemos el cartitem actualizamos cantidad
                log.info("si tenemos el cartitem actualizamos cantidad");
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
            } else {// no tenemos el cartItem, lo añadimos
                log.info("no tenemos el cartItem, lo añadimos");
                cartItem = CartItem.builder()
                        .idProduct(idProduct)
                        .nameProduct(productDTO.getName())
                        .quantity(quantity)
                        .unitPrice(productDTO.getPrice())
                        .cart(cart)
                        .build();
                cart.getCartItems().add(cartItem);
            }

            log.info("productServiceClient.updateStockProduct");
            productServiceClient.updateStockProduct(
                    idProduct,
                    SaleDTO.builder()
                            .sale(quantity)
                            .build());

            Double newTotal = cart.getCartItems().stream()
                    .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                    .sum();

            cart.setTotal(newTotal);
        } else {
            throw new NoStockException("No hay suficiente stock");
        }

        log.info("cartRepository.save");
        return cartRepository.save(cart);
    }

    /***
     * Elimina un producto del carrito
     * @param idUser
     * @param idProduct
     * @return
     * @throws CartNotFoundException
     * @throws ProductNotFoundInCartException
     */
    public Cart removeToCart(Long idUser, Long idProduct) throws CartNotFoundException, ProductNotFoundInCartException {
        log.info("cartRepository.findByIdUser");
        Cart cart = cartRepository.findByIdUser(idUser);
        if (cart == null) {
            throw new CartNotFoundException("El carrito asociado al usuario no existe");
        }

        // buscamos el producto a eliminar
        log.info("buscamos el producto a eliminar");
        CartItem cartItemToDelete = cart.getCartItems().stream()
                .filter(item -> item.getIdProduct().equals(idProduct))
                .findFirst()
                .orElse(null);

        if (cartItemToDelete == null) {
            throw new ProductNotFoundInCartException("El producto no esta en el carrito");
        }

        cart.getCartItems().remove(cartItemToDelete);
        // recalculamos el total
        Double newTotal = cart.getCartItems().stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();

        cart.setTotal(newTotal);

        log.info("cartRepository.save");
        return cartRepository.save(cart);
    }

    public void clearCart(Long idUser) {
        log.info("clearCart");
        log.info("cartRepository.findByIdUser");
        Cart cart = cartRepository.findByIdUser(idUser);
        cart.setTotal(0.0);
        cart.getCartItems().clear();
        log.info("cartRepository.save");
        cartRepository.save(cart);
    }
}
