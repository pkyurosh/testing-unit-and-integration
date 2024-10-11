package com.ecom.shopping_cart.service;

import com.ecom.shopping_cart.model.Category;

import java.util.List;

public interface CategoryService {
    public Category saveCategory(Category category);


    public Boolean existCategory(String Name);


    public List <Category> getAllCategory();

    public Boolean deleteCategory(int id);

    public Category getCategoryById(int id);

    public List <Category> getAllActiveCategory();
}
