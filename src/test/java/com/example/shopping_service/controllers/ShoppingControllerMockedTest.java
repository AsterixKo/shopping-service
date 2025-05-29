package com.example.shopping_service.controllers;

import com.example.shopping_service.exceptions.*;
import com.example.shopping_service.models.dtos.RequestAddToCartDTO;
import com.example.shopping_service.models.dtos.RequestRemoveFromCartDTO;
import com.example.shopping_service.models.entities.Cart;
import com.example.shopping_service.models.entities.CartItem;
import com.example.shopping_service.services.ShoppingService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest
public class ShoppingControllerMockedTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShoppingService shoppingService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("añadir al carrito")
    public void addToCartTest()
            throws Exception {
        List<CartItem> cartItemList = new ArrayList<>();
        CartItem cartItem = CartItem.builder()
                .id(1L)
                .unitPrice(200.0)
                .quantity(1)
                .idProduct(1L)
                .nameProduct("Teclado con cable")
                .build();
        cartItemList.add(cartItem);

        Cart cart = Cart.builder()
                .id(1L)
                .email("testing_email@test.com")
                .idUser(1L)
                .cartItems(cartItemList)
                .total(200.0)
                .build();
        cartItem.setCart(cart);

        when(shoppingService.addToCart(1L, 1L, 1)).thenReturn(cart);

        RequestAddToCartDTO requestAddToCartDTO = RequestAddToCartDTO.builder()
                .idProduct(1L)
                .idUser(1L)
                .quantity(1)
                .build();

        String requestAddToCartDTOJson = objectMapper.writeValueAsString(requestAddToCartDTO);

        MvcResult result = mockMvc.perform(post("/api/shopping/add-to-cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestAddToCartDTOJson))
                .andExpect(status().isOk()).andReturn();

        String stringResponse = result.getResponse().getContentAsString();
        log.info(stringResponse);
        assertTrue(stringResponse.contains("Teclado con cable"));
    }

    @Test
    @DisplayName("eliminar del carrito")
    public void removeFromCart() throws Exception {
        Cart cart = Cart.builder()
                .cartItems(new ArrayList<>())
                .total(0.0)
                .id(1L)
                .idUser(1L)
                .email("test_email@test.com")
                .build();

        when(shoppingService.removeToCart(1L, 1L)).thenReturn(cart);

        RequestRemoveFromCartDTO requestRemoveFromCartDTO = RequestRemoveFromCartDTO.builder()
                .idUser(1L)
                .idProduct(1L)
                .build();

        String requestRemoveFromCartDTOJson = objectMapper.writeValueAsString(requestRemoveFromCartDTO);

        MvcResult result = mockMvc.perform(delete("/api/shopping/remove-from-cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestRemoveFromCartDTOJson))
                .andExpect(status().isOk()).andReturn();

        String stringResponse = result.getResponse().getContentAsString();
        log.info(stringResponse);
        assertTrue(stringResponse.contains("test_email@test.com"));
    }

    @Test
    @DisplayName("limpiar el carrito")
    public void cleanCart() throws Exception {

        doNothing().when(shoppingService).clearCart(1L);

        MvcResult result = mockMvc.perform(get("/api/shopping/clear-cart/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String stringResponse = result.getResponse().getContentAsString();
        log.info(stringResponse);
        assertTrue(stringResponse.contains("Carrito vaciado"));
    }
}
