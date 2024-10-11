package com.ecom.shopping_cart;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


import java.io.IOException;
import java.util.*;

import com.ecom.shopping_cart.model.Product;
import com.ecom.shopping_cart.repository.ProductRepository;
import com.ecom.shopping_cart.service.impl.ProductServiceImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ProductServiceUnitTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductServiceImpl productService;

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        Product product1 = new Product();
        product1.setId(1);
        product1.setTitle("Necklace");

        List<Product> productList = new ArrayList<>();
        productList.add(product1);

        Mockito.when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(productRepository.findAll()).thenReturn(productList);
        Mockito.when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        Mockito.when(productRepository.findById(99)).thenReturn(Optional.empty());  // Failure case
        Mockito.doNothing().when(productRepository).deleteById(1);
        Mockito.doThrow(new RuntimeException("Product not found")).when(productRepository).deleteById(99);
    }

    // Success and Failure Test Cases for saveProduct

    @Test
    void testSaveProductSuccess() {
        Product product = new Product();
        product.setTitle("Bracelet");

        Product savedProduct = productService.saveProduct(product);

        assertEquals("Bracelet", savedProduct.getTitle());
        assertThatNoException();
    }

    @Test
    void testSaveProductFailure() {
        Product product = new Product();
        product.setTitle(null);  // Simulating a validation failure

        // Assuming there is validation in the actual service method that throws an exception
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertTrue(!violations.isEmpty());
    }

    // Success and Failure Test Cases for getAllProducts

    @Test
    void testGetAllProductsSuccess() {
        List<Product> products = productService.getAllProducts();

        assertFalse(products.isEmpty());
        assertEquals(1, products.size());
    }

    @Test
    void testGetAllProductsFailure() {
        Mockito.when(productRepository.findAll()).thenReturn(Collections.emptyList());

        List<Product> products = productService.getAllProducts();

        assertTrue(products.isEmpty());
    }

    // Success and Failure Test Cases for deleteProduct

    @Test
    void testDeleteProductSuccess() {
        boolean result = productService.deleteProduct(1);

        assertTrue(result);
        assertThatNoException();
    }

    @Test
    void testDeleteProductFailure() {
        assertFalse( () -> {
            return  productService.deleteProduct(99);  // ID not found
        });
    }

    // Success and Failure Test Cases for getProductById

    @Test
    void testGetProductByIdSuccess() {
        Product product = productService.getProductById(1);

        assertNotNull(product);
        assertEquals("Necklace", product.getTitle());
    }

    @Test
    void testGetProductByIdFailure() {
        Product product = productService.getProductById(99);

        assertNull(product);
    }
    // Success and Failure Test Cases for updateProduct

    @Test
    void testUpdateProductSuccess() throws IOException {
        Product productToUpdate = new Product();
        productToUpdate.setId(1);
        productToUpdate.setTitle("Updated Necklace");
        productToUpdate.setDescription("Updated Gold Necklace");
        productToUpdate.setPrice(150.0);
        productToUpdate.setStock(20);
        productToUpdate.setCategory("Jewelry");

        MultipartFile mockImage = new MockMultipartFile("image", "newImage.jpg", "image/jpeg", new byte[10]);

        Product updatedProduct = productService.updateProduct(productToUpdate, mockImage);

        assertNotNull(updatedProduct);
        assertEquals("Updated Necklace", updatedProduct.getTitle());
    }

    @Test
    void testUpdateProductFailureProductNotFound() {
        Product productToUpdate = new Product();
        productToUpdate.setId(99); // Non-existent product
        productToUpdate.setTitle("Non-existent Product");

        MultipartFile mockImage = new MockMultipartFile("image", "ecom1.png", "image/jpeg", new byte[10]);

        Mockito.when(productRepository.findById(99)).thenReturn(Optional.empty());  // Simulating product not found

        assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(productToUpdate, mockImage);
        });
    }

    @Test
    void testUpdateProductFailureWithEmptyImage() throws IOException {
        Product productToUpdate = new Product();
        productToUpdate.setId(1);
        productToUpdate.setTitle("Updated Necklace");
        productToUpdate.setDescription("Updated Gold Necklace");

        MultipartFile emptyImage = new MockMultipartFile("image", "", "image/jpeg", new byte[0]);

        Product updatedProduct = productService.updateProduct(productToUpdate, emptyImage);

        assertNotNull(updatedProduct);
        assertEquals("Updated Necklace", updatedProduct.getTitle());  // Image should not change
    }
}

