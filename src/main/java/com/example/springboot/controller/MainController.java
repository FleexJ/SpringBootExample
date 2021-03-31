package com.example.springboot.controller;

import com.example.springboot.entity.User;
import com.example.springboot.service.UserService;
import com.example.springboot.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {

    UserValidator userValidator;
    UserService userService;

    @Autowired
    public MainController(UserValidator userValidator, UserService userService) {
        this.userValidator = userValidator;
        this.userService = userService;
    }

    @GetMapping("/")
    public String indexPageGET() {
        return "index";
    }

    @GetMapping("/sign_up")
    public String signUpPageGET(@ModelAttribute("user") User user) {
        return "sign_up";
    }

    @PostMapping("/sign_up")
    public String signUpPagePOST(@ModelAttribute User user,
                                 BindingResult result) {
        userValidator.validate(user, result);
        if (result.hasErrors())
            return "sign_up";
        userService.addUser(user);
        return "redirect:/";
    }
}
