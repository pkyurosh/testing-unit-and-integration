package com.ecom.shopping_cart;




import com.ecom.shopping_cart.model.Product;
import com.ecom.shopping_cart.repository.ProductRepository;

import com.ecom.shopping_cart.service.ProductService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ShoppingCartApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class AdminControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void setUp() {

        Product product = new Product();
        product.setTitle("Laptop");
        product.setDescription("High-end gaming laptop");
        product.setPrice(1200.0);
        product.setStock(15);

        product.setImage("laptop.jpg");
        productRepository.save(product);
    }

    @AfterEach
    public void tearDown() {
        productRepository.deleteAll();
    }




    @Test
    public void testSaveProduct() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("file", "laptop.jpg", "image/jpeg", new byte[10]);

        mockMvc.perform(multipart("/admin/saveProduct")
                        .file(imageFile)
                        .param("title", "Smartphone")
                        .param("description", "Latest smartphone")
                        .param("price", "900")
                        .param("stock", "20")
                        .param("category.id", "1"));


        List<Product> products = productRepository.findAll();
        assertEquals(2, products.size()); // Assuming 1 product is set up in @BeforeEach
    }


    @Test
    public void testSaveProductMissingTitle() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("file", "laptop.jpg", "image/jpeg", new byte[10]);

        mockMvc.perform(multipart("/admin/saveProduct")
                        .file(imageFile)
                        .param("description", "Latest smartphone")
                        .param("price", "900")
                        .param("stock", "20")
                        .param("category.id", "1"))
                .andExpect(status().isBadRequest());

        // Verify the product is NOT saved in the repository
        List<Product> products = productRepository.findAll();
        assertEquals(1, products.size()); // No new product should have been added
    }

    // Failure case: Missing name parameter for category



    @Test
    public void testDeleteProduct() throws Exception {
        Product product = productRepository.findByTitle("Laptop");

        mockMvc.perform(get("/admin/deleteProduct/" + product.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));

        assertFalse(productRepository.existsById(product.getId()));
    }

    @Test
    public void testUpdateProduct() throws Exception {
        Product product = productRepository.findByTitle("Laptop");
        MockMultipartFile imageFile = new MockMultipartFile("file", "updated_laptop.jpg", "image/jpeg", new byte[10]);

        mockMvc.perform(multipart("/admin/updateProduct")
                        .file(imageFile)
                        .param("id", String.valueOf(product.getId()))
                        .param("title", "Updated Laptop")
                        .param("description", "Updated description")
                        .param("price", "1500")
                        .param("stock", "10")
                        .param("category.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/editProduct/" + product.getId()));

        Product updatedProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + product.getId()));

        assertEquals("Updated Laptop", updatedProduct.getTitle());
        assertEquals("updated_laptop.jpg", updatedProduct.getImage());
    }
}
