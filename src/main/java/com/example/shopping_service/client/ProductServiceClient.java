package com.example.shopping_service.client;

import com.example.shopping_service.models.dtos.ProductDTO;
import com.example.shopping_service.models.dtos.SaleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service")
public interface ProductServiceClient {

    @GetMapping("/api/product/{idProduct}")
    ProductDTO findProductById(@PathVariable("idProduct") Long idProduct);

    @PutMapping("/api/product/update/stock/{idProduct}")
    ProductDTO updateStockProduct(@PathVariable("idProduct") Long idProduct, @RequestBody SaleDTO saleDTO);
}
