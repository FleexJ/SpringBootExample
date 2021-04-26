package com.example.springboot.controller;

import com.example.springboot.entity.Role;
import com.example.springboot.entity.User;
import com.example.springboot.service.NoteService;
import com.example.springboot.service.UserService;
import com.example.springboot.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {

    private final UserValidator userValidator;
    private final UserService userService;
    private final NoteService noteService;

    @Autowired
    public MainController(UserValidator userValidator,
                          UserService userService,
                          NoteService noteService) {
        this.userValidator = userValidator;
        this.userService = userService;
        this.noteService = noteService;
    }

    @GetMapping("/")
    public String indexGET(@AuthenticationPrincipal User user,
                           Model model) {
        model.addAttribute("currentUser", user);
        model.addAttribute("notes", noteService.getAllNotes());
        return "index";
    }


    @GetMapping("/sign_up")
    public String signUpGET(@ModelAttribute("user") User user) {
        return "/auth/sign_up";
    }

    @PostMapping("/sign_up")
    public String signUpPOST(@ModelAttribute User user,
                             BindingResult result) {
        userValidator.validate(user, result);
        if (result.hasErrors())
            return "/auth/sign_up";
        user.setRole(Role.ADMIN);
        userService.addUser(user);
        System.out.println("New user: " + user.getEmail());
        return "redirect:/sign_in";
    }


    @GetMapping("/sign_in")
    public String signInGET(@RequestParam(value = "error", required = false) Boolean error,
                            Model model) {
        if (error != null && error)
            model.addAttribute("error", true);
        return "/auth/sign_in";
    }
}
