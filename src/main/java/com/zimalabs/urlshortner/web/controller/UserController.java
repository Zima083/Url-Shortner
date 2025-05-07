package com.zimalabs.urlshortner.web.controller;

import com.zimalabs.urlshortner.domain.entities.Services.UserService;
import com.zimalabs.urlshortner.domain.entities.models.CreateShortUrlCmd;
import com.zimalabs.urlshortner.domain.entities.models.Role;
import com.zimalabs.urlshortner.web.controller.dtos.CreateUserCmd;
import com.zimalabs.urlshortner.web.controller.dtos.RegisterUserRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
   private final UserService userServices;

    public UserController(UserService userService) {
        this.userServices = userService;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new RegisterUserRequest("","",""));
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") RegisterUserRequest registerRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            var cmd = new CreateUserCmd(
                    registerRequest.email(),
                    registerRequest
                            .password(),
                    registerRequest.name(),
                    Role.ROLE_USER
            );
            userServices.createUser(cmd);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
    }
}
