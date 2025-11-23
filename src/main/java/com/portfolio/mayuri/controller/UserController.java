package com.portfolio.mayuri.controller;

import com.portfolio.mayuri.Entity.User;
import com.portfolio.mayuri.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "https://my-portfolio-fjdr.vercel.app/") // frontend origin
public class UserController {

    @Autowired
    private UserRepo userRepository;

    // Register endpoint
    @PostMapping("/register")
    public Response registerUser(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            return new Response("exists", "User already exists. Please login.");
        }

        try {
            userRepository.save(user);
            return new Response("success", "User registered successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response("error", "Registration failed. Try again.");
        }
    }

    // Login endpoint
    @PostMapping("/login")
    public LoginResponse loginUser(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmailAndPassword(request.getEmail(), request.getPassword());

        if (userOpt.isPresent()) {
            return new LoginResponse("success", userOpt.get());
        } else {
            return new LoginResponse("fail", null);
        }
    }

    // Inner classes for request/response
    static class Response {
        private String status;
        private String message;

        public Response(String status, String message) {
            this.status = status;
            this.message = message;
        }

        // getters & setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    static class LoginRequest {
        private String email;
        private String password;

        // getters & setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    static class LoginResponse {
        private String status;
        private User user;

        public LoginResponse(String status, User user) {
            this.status = status;
            this.user = user;
        }

        // getters & setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
    }
}
