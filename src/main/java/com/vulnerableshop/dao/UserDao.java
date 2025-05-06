package com.vulnerableshop.dao;

import java.util.List;

import com.vulnerableshop.model.User;

public interface UserDao {
    
    User findByUsername(String username);
    
    User findByEmail(String email);
    
    User findById(Long id);
    
    void save(User user);
    
    void update(User user);
    
    void delete(User user);
    
    List<User> findAll();
}