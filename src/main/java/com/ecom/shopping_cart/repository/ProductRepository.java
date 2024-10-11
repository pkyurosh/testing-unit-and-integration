package com.ecom.shopping_cart.repository;

import com.ecom.shopping_cart.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByIsActiveTrue();

    List<Product> findByCategory(String category);

    Product findByTitle(String product);


}
