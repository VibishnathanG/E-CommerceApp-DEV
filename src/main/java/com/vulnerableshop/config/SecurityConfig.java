package com.vulnerableshop.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Vulnerable: Using in-memory authentication with hard-coded credentials
        auth.inMemoryAuthentication()
            .withUser("admin")
            .password("admin123")
            .roles("ADMIN");
        
        // Also using database authentication but with insecure password encoding
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Vulnerable configurations:
        
        // 1. Disabled CSRF protection
        http.csrf().disable();
        
        // 2. No strict transport security
        
        // 3. Permissive CORS configuration
        http.cors();
        
        // 4. Basic auth without HTTPS requirement
        http.httpBasic();
        
        // 5. Insecure session management
        http.sessionManagement()
            .invalidSessionUrl("/login");
        
        // 6. Overly permissive authorization rules
        http.authorizeRequests()
            .antMatchers("/admin/**").hasRole("ADMIN")
            .antMatchers("/cart/**").authenticated()
            .anyRequest().permitAll()
            .and()
            .formLogin()
            .loginPage("/login")
            .permitAll()
            .and()
            .logout()
            .permitAll();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Vulnerable: Using deprecated NoOpPasswordEncoder that stores passwords in plain text
        return NoOpPasswordEncoder.getInstance();
    }
}