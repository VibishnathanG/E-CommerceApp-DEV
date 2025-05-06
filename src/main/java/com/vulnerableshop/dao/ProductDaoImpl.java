package com.vulnerableshop.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.vulnerableshop.model.Product;

@Repository
public class ProductDaoImpl implements ProductDao {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    @Override
    public Product findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Product.class, id);
    }
    
    @Override
    public List<Product> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Product", Product.class).list();
    }
    
    @Override
    public List<Product> findByCategory(String category) {
        Session session = sessionFactory.getCurrentSession();
        Query<Product> query = session.createQuery("from Product where category = :category", Product.class);
        query.setParameter("category", category);
        return query.list();
    }
    
    @Override
    public List<Product> search(String keyword) {
        Session session = sessionFactory.getCurrentSession();
        
        // Vulnerable: Using unvalidated input in HQL query
        String hql = "from Product where name like '%" + keyword + "%' or description like '%" + keyword + "%'";
        return session.createQuery(hql, Product.class).list();
    }
    
    @Override
    public void save(Product product) {
        Session session = sessionFactory.getCurrentSession();
        session.save(product);
    }
    
    @Override
    public void update(Product product) {
        Session session = sessionFactory.getCurrentSession();
        session.update(product);
    }
    
    @Override
    public void delete(Product product) {
        Session session = sessionFactory.getCurrentSession();
        session.delete(product);
    }
}