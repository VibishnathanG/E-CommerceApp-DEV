package com.vulnerableshop.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vulnerableshop.model.Product;
import com.vulnerableshop.service.ProductService;

@Controller
@RequestMapping("/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public String listProducts(Model model) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "products/list";
    }
    
    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        return "products/view";
    }
    
    // Vulnerable: No CSRF protection
    @PostMapping("/add-to-cart/{id}")
    public String addToCart(@PathVariable Long id) {
        // Add to cart logic...
        return "redirect:/cart";
    }
    
    @GetMapping("/category/{category}")
    public String listByCategory(@PathVariable String category, Model model) {
        // Vulnerable: No input validation for category
        List<Product> products = productService.findByCategory(category);
        model.addAttribute("products", products);
        model.addAttribute("category", category);
        return "products/category";
    }
    
    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        // Vulnerable: No input validation for search keyword
        List<Product> products = productService.search(keyword);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "products/search";
    }
    
    // Vulnerable: Reflected XSS
    @GetMapping("/searchjson")
    @ResponseBody
    public String searchJson(@RequestParam String keyword) {
        List<Product> products = productService.search(keyword);
        
        // Vulnerable: Directly reflecting user input
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"query\": \"").append(keyword).append("\", \"results\": [");
        
        for (Product product : products) {
            sb.append("{\"id\":").append(product.getId())
              .append(",\"name\":\"").append(product.getName())
              .append("\",\"price\":").append(product.getPrice())
              .append("},");
        }
        
        if (!products.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        
        sb.append("]}");
        
        return sb.toString();
    }
    
    // Vulnerable: Open redirect
    @GetMapping("/redirect")
    public void redirect(@RequestParam String url, HttpServletResponse response) throws IOException {
        // Vulnerable: No validation of URL
        response.sendRedirect(url);
    }
    
    // Admin functions with no proper authorization
    @GetMapping("/admin/add")
    public String addProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "products/admin/add";
    }
    
    @PostMapping("/admin/add")
    public String addProduct(@ModelAttribute Product product) {
        // Vulnerable: No input validation
        productService.save(product);
        return "redirect:/products";
    }
    
    @GetMapping("/admin/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        return "products/admin/edit";
    }
    
    @PostMapping("/admin/edit/{id}")
    public String editProduct(@PathVariable Long id, @ModelAttribute Product product) {
        // Vulnerable: No input validation
        productService.update(product);
        return "redirect:/products";
    }
    
    @PostMapping("/admin/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        Product product = productService.findById(id);
        if (product != null) {
            productService.delete(product);
        }
        return "redirect:/products";
    }
}