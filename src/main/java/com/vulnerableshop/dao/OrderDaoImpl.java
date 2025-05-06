package com.vulnerableshop.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vulnerableshop.model.Order;
import com.vulnerableshop.model.User;

@Repository
public class OrderDaoImpl implements OrderDao {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    @Override
    public Order findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Order.class, id);
    }
    
    @Override
    public List<Order> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Order", Order.class).list();
    }
    
    @Override
    public List<Order> findByUser(User user) {
        Session session = sessionFactory.getCurrentSession();
        Query<Order> query = session.createQuery("from Order where user = :user", Order.class);
        query.setParameter("user", user);
        return query.list();
    }
    
    @Override
    public void save(Order order) {
        Session session = sessionFactory.getCurrentSession();
        session.save(order);
    }
    
    @Override
    public void update(Order order) {
        Session session = sessionFactory.getCurrentSession();
        session.update(order);
    }
    
    @Override
    public void delete(Order order) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(order);
    }
}