package com.vulnerableshop.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.vulnerableshop.model.User;
import com.vulnerableshop.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "users/register";
    }
    
    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        // Vulnerable: No input validation or sanitization
        userService.save(user);
        return "redirect:/users/login";
    }
    
    @GetMapping("/login")
    public String loginForm() {
        return "users/login";
    }
    
    // Vulnerable: Custom login implementation instead of using Spring Security
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, 
                        HttpSession session, Model model) {
        
        if (userService.authenticate(username, password)) {
            User user = userService.findByUsername(username);
            session.setAttribute("user", user);
            return "redirect:/";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "users/login";
        }
    }
    
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }
        
        model.addAttribute("user", user);
        return "users/profile";
    }
    
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        // Vulnerable: No authorization check
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "users/edit";
    }
    
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @ModelAttribute User user) {
        // Vulnerable: No authorization check
        userService.update(user);
        return "redirect:/users/profile";
    }
    
    // Vulnerable: Insecure file upload
    @PostMapping("/upload-avatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file, 
                              @RequestParam("userId") Long userId,
                              HttpServletRequest request) {
        
        if (!file.isEmpty()) {
            try {
                String uploadsDir = request.getServletContext().getRealPath("/uploads/");
                File directory = new File(uploadsDir);
                if (!directory.exists()) {
                    directory.mkdir();
                }
                
                // Vulnerable: Using user-controlled filename
                String filename = file.getOriginalFilename();
                String filePath = uploadsDir + filename;
                
                // Save the file
                file.transferTo(new File(filePath));
                
                // Update user profile with avatar URL
                User user = userService.findById(userId);
                user.setBio("<img src='/uploads/" + filename + "' />");
                userService.update(user);
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return "redirect:/users/profile";
    }
    
    // Vulnerable: Path traversal
    @GetMapping("/download")
    public String downloadFile(@RequestParam("file") String filename, HttpServletRequest request) {
        try {
            String uploadsDir = request.getServletContext().getRealPath("/uploads/");
            
            // Vulnerable: No path validation
            String filePath = uploadsDir + filename;
            
            // Read and return file
            byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
            // Proceed with download...
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return "redirect:/users/profile";
    }
    
    // Vulnerable: No authorization for admin functions
    @GetMapping("/admin/users")
    public String listUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "users/admin/list";
    }
    
    // Vulnerable: Command injection
    @PostMapping("/admin/execute")
    public String executeCommand(@RequestParam("command") String command) {
        try {
            // Vulnerable: Direct execution of user input
            Process process = Runtime.getRuntime().exec(command);
            // Handle process output...
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return "redirect:/users/admin/users";
    }
}