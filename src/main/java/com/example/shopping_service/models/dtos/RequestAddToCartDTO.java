package com.example.shopping_service.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestAddToCartDTO {
    private Long idUser;
    private Long idProduct;
    private Integer quantity;
}
