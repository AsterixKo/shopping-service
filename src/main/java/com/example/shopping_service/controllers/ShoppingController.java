package com.example.shopping_service.controllers;

import com.example.shopping_service.exceptions.*;
import com.example.shopping_service.models.dtos.RequestAddToCartDTO;
import com.example.shopping_service.models.dtos.RequestRemoveFromCartDTO;
import com.example.shopping_service.models.entities.Cart;
import com.example.shopping_service.services.ShoppingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/shopping")
public class ShoppingController {

    @Autowired
    private ShoppingService shoppingService;

    @PostMapping("/add-to-cart")
    public ResponseEntity<?> addToCart(@RequestBody RequestAddToCartDTO requestAddToCartDTO) {
        try {
            Cart cart = shoppingService.addToCart(
                    requestAddToCartDTO.getIdUser(),
                    requestAddToCartDTO.getIdProduct(),
                    requestAddToCartDTO.getQuantity());
            return new ResponseEntity<>(cart, HttpStatus.OK);
        } catch (ProductNotFoundException e) {
            log.error("Error producto no encontrado {}", e.getMessage());
            return new ResponseEntity<>("Error producto no encontrado", HttpStatus.BAD_REQUEST);
        } catch (NoStockException e) {
            log.error("Error no hay suficiente stock {}", e.getMessage());
            return new ResponseEntity<>("Error no hay suficiente stock", HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e) {
            log.error("Error usuario no encontrado {}", e.getMessage());
            return new ResponseEntity<>("Error usuario no encontrado", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error inesperado {}", e.getMessage());
            return new ResponseEntity<>("Error inesperado", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/remove-from-cart")
    public ResponseEntity<?> removeFromCart(@RequestBody RequestRemoveFromCartDTO requestRemoveFromCartDTO) {
        try {
            Cart cart = shoppingService.removeToCart(
                    requestRemoveFromCartDTO.getIdUser(),
                    requestRemoveFromCartDTO.getIdProduct());
            return new ResponseEntity<>(cart, HttpStatus.OK);
        } catch (CartNotFoundException e) {
            log.error("Error carrito no encontrado {}", e.getMessage());
            return new ResponseEntity<>("Error carrito no encontrado", HttpStatus.BAD_REQUEST);
        } catch (ProductNotFoundInCartException e) {
            log.error("Error el producto no esta en el carrito {}", e.getMessage());
            return new ResponseEntity<>("Error el producto no esta en el carrito", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error inesperado {}", e.getMessage());
            return new ResponseEntity<>("Error inesperado", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/clear-cart/{idUser}")
    public ResponseEntity<?> cleanCart(@PathVariable Long idUser) {
        try {
            shoppingService.clearCart(idUser);
            return new ResponseEntity<>("Carrito vaciado", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error inesperado {}", e.getMessage());
            return new ResponseEntity<>("Error inesperado", HttpStatus.BAD_REQUEST);
        }
    }
}
