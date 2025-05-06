package com.vulnerableshop.service;

import java.util.List;

import com.vulnerableshop.model.User;

public interface UserService {
    
    User findByUsername(String username);
    
    User findByEmail(String email);
    
    User findById(Long id);
    
    void save(User user);
    
    void update(User user);
    
    void delete(User user);
    
    List<User> findAll();
    
    boolean authenticate(String username, String password);
}