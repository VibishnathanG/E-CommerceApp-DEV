package com.vulnerableshop.service;

import java.util.List;

import com.vulnerableshop.model.Order;
import com.vulnerableshop.model.User;

public interface OrderService {
    
    Order findById(Long id);
    
    List<Order> findAll();
    
    List<Order> findByUser(User user);
    
    void save(Order order);
    
    void update(Order order);
    
    void delete(Order order);
}