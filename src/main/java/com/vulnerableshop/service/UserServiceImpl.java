package com.vulnerableshop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vulnerableshop.dao.UserDao;
import com.vulnerableshop.model.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User.UserBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    
    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());
    
    @Autowired
    private UserDao userDao;
    
    @Override
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }
    
    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }
    
    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userDao.findById(id);
    }
    
    @Override
    @Transactional
    public void save(User user) {
        // Vulnerable: No input validation
        
        // Vulnerable: No password hashing
        userDao.save(user);
        
        // Vulnerable: Logging sensitive data
        logger.info("New user created: " + user.toString());
    }
    
    @Override
    @Transactional
    public void update(User user) {
        userDao.update(user);
    }
    
    @Override
    @Transactional
    public void delete(User user) {
        userDao.delete(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userDao.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean authenticate(String username, String password) {
        // Vulnerable: Direct password comparison
        User user = userDao.findByUsername(username);
        if (user != null) {
            return user.getPassword().equals(password);
        }
        return false;
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);
        if (user == null) {
            // Vulnerable: Information disclosure in error message
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        
        // Vulnerable: Creating UserDetails with plain text password
        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(username);
        builder.password(user.getPassword());
        builder.roles(user.getRole().replaceAll("ROLE_", ""));
        
        // Log sensitive information
        logger.info("User logged in: " + user.toString());
        
        return builder.build();
    }
}