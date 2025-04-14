package com.apartment.management.controller;

import com.apartment.management.model.User;
import com.apartment.management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        // Set default role as resident
        user.setRoles(new HashSet<>());
        user.getRoles().add("ROLE_RESIDENT");
        
        userService.createUser(user);
        return "redirect:/login?registered";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userOptional = userService.getUserByUsername(auth.getName());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            model.addAttribute("user", user);
            
            if (user.isAdmin()) {
                return "admin/dashboard";
            } else {
                return "resident/dashboard";
            }
        }
        
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userOptional = userService.getUserByUsername(auth.getName());
        
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "profile";
        }
        
        return "redirect:/login";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User user) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> userOptional = userService.getUserByUsername(auth.getName());
        
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            existingUser.setName(user.getName());
            existingUser.setSurname(user.getSurname());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhoneNumber(user.getPhoneNumber());
            
            userService.updateUser(existingUser);
            return "redirect:/profile?updated";
        }
        
        return "redirect:/login";
    }
}
