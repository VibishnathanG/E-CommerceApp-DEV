package com.microsoft.example;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ecommerce")
public class EcommerceServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");

        var out = resp.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html><head>");
        out.println("<title>Simple E-Commerce</title>");
        out.println("<style>");
        out.println("body { font-family: Arial; background: #f9f9f9; margin: 0; padding: 0; }");
        out.println(".header { background: #333; color: #fff; padding: 1rem; text-align: center; }");
        out.println(".products { display: flex; flex-wrap: wrap; justify-content: center; margin: 2rem; }");
        out.println(".product { background: #fff; border: 1px solid #ddd; border-radius: 8px; margin: 1rem; padding: 1rem; width: 200px; box-shadow: 2px 2px 8px #ccc; }");
        out.println(".product img { max-width: 100%; border-radius: 4px; }");
        out.println(".product h3 { margin: 0.5rem 0; }");
        out.println(".product button { background: #28a745; color: white; border: none; padding: 0.5rem 1rem; border-radius: 5px; cursor: pointer; }");
        out.println(".product button:hover { background: #218838; }");
        out.println("</style>");
        out.println("</head><body>");

        out.println("<div class='header'><h1>VibesMart - Your Java Store</h1></div>");
        out.println("<div class='products'>");

        // Product 1
        out.println("<div class='product'>");
        out.println("<img src='https://via.placeholder.com/200x150?text=Product+1' alt='Product 1'/>");
        out.println("<h3>Wireless Headphones</h3>");
        out.println("<p>Price: ₹1999</p>");
        out.println("<button onclick='alert(\"Added to cart: Wireless Headphones\")'>Add to Cart</button>");
        out.println("</div>");

        // Product 2
        out.println("<div class='product'>");
        out.println("<img src='https://via.placeholder.com/200x150?text=Product+2' alt='Product 2'/>");
        out.println("<h3>Smart Watch</h3>");
        out.println("<p>Price: ₹3499</p>");
        out.println("<button onclick='alert(\"Added to cart: Smart Watch\")'>Add to Cart</button>");
        out.println("</div>");

        // Product 3
        out.println("<div class='product'>");
        out.println("<img src='https://via.placeholder.com/200x150?text=Product+3' alt='Product 3'/>");
        out.println("<h3>Bluetooth Speaker</h3>");
        out.println("<p>Price: ₹1499</p>");
        out.println("<button onclick='alert(\"Added to cart: Bluetooth Speaker\")'>Add to Cart</button>");
        out.println("</div>");

        out.println("</div>"); // end products
        out.println("</body></html>");
    }
}
