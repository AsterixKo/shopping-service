package com.example.shopping_service.repositories;

import com.example.shopping_service.models.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByIdUser(Long idUser);
}
