package com.vulnerableshop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vulnerableshop.dao.OrderDao;
import com.vulnerableshop.model.Order;
import com.vulnerableshop.model.User;

@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderDao orderDao;
    
    @Override
    @Transactional(readOnly = true)
    public Order findById(Long id) {
        return orderDao.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderDao.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Order> findByUser(User user) {
        return orderDao.findByUser(user);
    }
    
    @Override
    @Transactional
    public void save(Order order) {
        // Vulnerable: No validation of order data
        orderDao.save(order);
    }
    
    @Override
    @Transactional
    public void update(Order order) {
        // Vulnerable: No validation of order updates
        orderDao.update(order);
    }
    
    @Override
    @Transactional
    public void delete(Order order) {
        orderDao.delete(order);
    }
}