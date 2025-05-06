package com.vulnerableshop.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vulnerableshop.model.User;

@Repository
public class UserDaoImpl implements UserDao {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    @Autowired
    private DataSource dataSource;
    
    @Override
    public User findByUsername(String username) {
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery("from User where username = :username", User.class);
        query.setParameter("username", username);
        return query.uniqueResult();
    }
    
    @Override
    public User findByEmail(String email) {
        Session session = sessionFactory.getCurrentSession();
        Query<User> query = session.createQuery("from User where email = :email", User.class);
        query.setParameter("email", email);
        return query.uniqueResult();
    }
    
    @Override
    public User findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(User.class, id);
    }
    
    @Override
    public void save(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.save(user);
    }
    
    @Override
    public void update(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.update(user);
    }
    
    @Override
    public void delete(User user) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(user);
    }
    
    @Override
    public List<User> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from User", User.class).list();
    }
    
    // Vulnerable method - SQL Injection
    public User findByUsernameVulnerable(String username) {
        try {
            Connection conn = dataSource.getConnection();
            
            // Vulnerable SQL query - direct string concatenation
            String sql = "SELECT * FROM users WHERE username = '" + username + "'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            ResultSet rs = stmt.executeQuery();
            User user = null;
            
            if (rs.next()) {
                user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Another vulnerable method - SQL Injection in query
    public boolean authenticateUser(String username, String password) {
        try {
            Connection conn = dataSource.getConnection();
            
            // Vulnerable SQL query using string concatenation
            String sql = "SELECT * FROM users WHERE username = '" + username + 
                         "' AND password = '" + password + "'";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            boolean authenticated = rs.next();
            
            rs.close();
            stmt.close();
            conn.close();
            
            return authenticated;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}