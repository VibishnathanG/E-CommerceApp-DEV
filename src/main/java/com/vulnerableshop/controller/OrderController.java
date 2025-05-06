package com.vulnerableshop.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vulnerableshop.model.Order;
import com.vulnerableshop.model.Product;
import com.vulnerableshop.model.User;
import com.vulnerableshop.service.OrderService;
import com.vulnerableshop.service.ProductService;
import com.vulnerableshop.service.UserService;

@Controller
@RequestMapping("/orders")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public String orderHistory(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }
        
        List<Order> orders = orderService.findByUser(user);
        model.addAttribute("orders", orders);
        
        return "orders/history";
    }
    
    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, HttpSession session, Model model) {
        // Vulnerable: No proper authorization check
        Order order = orderService.findById(id);
        model.addAttribute("order", order);
        
        return "orders/view";
    }
    
    // Vulnerable: Insecure direct object reference
    @GetMapping("/invoice/{id}")
    public String viewInvoice(@PathVariable Long id, Model model) {
        // No check if the current user is authorized to view this invoice
        Order order = orderService.findById(id);
        model.addAttribute("order", order);
        
        return "orders/invoice";
    }
    
    // Vulnerable: Directly placing order without CSRF protection
    @PostMapping("/place")
    public String placeOrder(
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity,
            @RequestParam("cardNumber") String cardNumber,
            @RequestParam("address") String address,
            HttpSession session) {
        
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }
        
        Product product = productService.findById(productId);
        
        // Create new order
        Order order = new Order();
        order.setUser(user);
        
        // Vulnerable: Storing payment information
        order.setCardNumber(cardNumber);
        order.setShippingAddress(address);
        
        // Calculate total
        BigDecimal totalAmount = product.getPrice().multiply(new BigDecimal(quantity));
        order.setTotalAmount(totalAmount);
        
        // Add product to order
        order.addProduct(product);
        
        // Save order
        orderService.save(order);
        
        return "redirect:/orders";
    }
    
    // Vulnerable: Server-side template injection
    @GetMapping("/email-preview")
    @ResponseBody
    public String emailPreview(@RequestParam("template") String template, @RequestParam("orderId") Long orderId) {
        Order order = orderService.findById(orderId);
        
        // Vulnerable: Direct template injection
        String result = template.replace("{{orderId}}", order.getId().toString())
                               .replace("{{customerName}}", order.getUser().getUsername())
                               .replace("{{amount}}", order.getTotalAmount().toString());
        
        return result;
    }
    
    // Vulnerable: Mass assignment
    @PostMapping("/update/{id}")
    public String updateOrder(@PathVariable Long id, @RequestParam("status") String status, 
                             @RequestParam("amount") BigDecimal amount) {
        
        Order order = orderService.findById(id);
        
        // Vulnerable: No validation on status change
        order.setStatus(status);
        
        // Vulnerable: Allowing price manipulation
        order.setTotalAmount(amount);
        
        orderService.update(order);
        
        return "redirect:/orders/" + id;
    }
    
    // Admin functions with improper access control
    @GetMapping("/admin/all")
    public String allOrders(Model model) {
        // Vulnerable: No admin check
        List<Order> orders = orderService.findAll();
        model.addAttribute("orders", orders);
        
        return "orders/admin/list";
    }
    
    @PostMapping("/admin/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        // Vulnerable: No admin check
        Order order = orderService.findById(id);
        if (order != null) {
            orderService.delete(order);
        }
        
        return "redirect:/orders/admin/all";
    }
}