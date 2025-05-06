package com.vulnerableshop.dao;

import java.util.List;

import com.vulnerableshop.model.Product;

public interface ProductDao {
    
    Product findById(Long id);
    
    List<Product> findAll();
    
    List<Product> findByCategory(String category);
    
    List<Product> search(String keyword);
    
    void save(Product product);
    
    void update(Product product);
    
    void delete(Product product);
}