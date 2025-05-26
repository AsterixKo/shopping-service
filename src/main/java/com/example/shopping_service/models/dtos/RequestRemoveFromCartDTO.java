package com.example.shopping_service.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestRemoveFromCartDTO {
    private Long idUser;
    private Long idProduct;
}
