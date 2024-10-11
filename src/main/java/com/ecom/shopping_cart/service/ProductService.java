package com.ecom.shopping_cart.service;

import java.util.List;

import com.ecom.shopping_cart.model.Product;
import org.springframework.web.multipart.MultipartFile;



public interface ProductService {

    public Product saveProduct(Product product);

    public List<Product> getAllProducts();

    public Boolean deleteProduct(Integer id);

    public Product getProductById(Integer id);

    public Product updateProduct(Product product, MultipartFile file);

    public List<Product> getAllActiveProducts();

}