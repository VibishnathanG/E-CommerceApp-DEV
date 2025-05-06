package com.vulnerableshop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vulnerableshop.dao.ProductDao;
import com.vulnerableshop.model.Product;

@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductDao productDao;
    
    @Override
    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return productDao.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productDao.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Product> findByCategory(String category) {
        return productDao.findByCategory(category);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Product> search(String keyword) {
        // Vulnerable: No input validation before passing to DAO
        return productDao.search(keyword);
    }
    
    @Override
    @Transactional
    public void save(Product product) {
        // Vulnerable: No validation of product data
        productDao.save(product);
    }
    
    @Override
    @Transactional
    public void update(Product product) {
        // Vulnerable: No validation of product data
        productDao.update(product);
    }
    
    @Override
    @Transactional
    public void delete(Product product) {
        productDao.delete(product);
    }
}