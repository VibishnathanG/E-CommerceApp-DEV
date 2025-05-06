package com.vulnerableshop.dao;

import java.util.List;

import com.vulnerableshop.model.Order;
import com.vulnerableshop.model.User;

public interface OrderDao {
    
    Order findById(Long id);
    
    List<Order> findAll();
    
    List<Order> findByUser(User user);
    
    void save(Order order);
    
    void update(Order order);
    
    void delete(Order order);
}