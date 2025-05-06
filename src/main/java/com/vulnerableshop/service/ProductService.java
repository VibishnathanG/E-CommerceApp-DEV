package com.vulnerableshop.service;

import java.util.List;

import com.vulnerableshop.model.Product;

public interface ProductService {
    
    Product findById(Long id);
    
    List<Product> findAll();
    
    List<Product> findByCategory(String category);
    
    List<Product> search(String keyword);
    
    void save(Product product);
    
    void update(Product product);
    
    void delete(Product product);
}