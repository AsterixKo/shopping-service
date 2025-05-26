package com.example.shopping_service.exceptions;

public class ProductNotFoundInCartException extends Exception {
    public ProductNotFoundInCartException(String message) {
        super(message);
    }
}
