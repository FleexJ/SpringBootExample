package com.example.springboot.controller;

import com.example.springboot.entity.User;
import com.example.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/my_profile")
public class MyProfileController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MyProfileController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("")
    public String myProfileGET(@AuthenticationPrincipal User user,
                               Model model) {
        model.addAttribute("currentUser", user);
        return "my_profile";
    }

    @GetMapping("/edit")
    public String editMyProfileGET(@AuthenticationPrincipal User user,
                                   @RequestParam(required = false, value = "errorEmail") Boolean errorEmail,
                                   @RequestParam(required = false, value = "errorName") Boolean errorName,
                                   Model model) {
        if (userService.getById(user.getId()) == null)
            return "redirect:/";

        if (errorEmail != null && errorEmail)
            model.addAttribute("errorEmail", true);
        if (errorName != null && errorName)
            model.addAttribute("errorName", true);
        model.addAttribute("currentUser", user);
        return "editMyProfile";
    }

    @PostMapping("/edit")
    public String editMyProfilePOST(@AuthenticationPrincipal User user,
                                    @RequestParam(value = "email") String email,
                                    @RequestParam(value = "name") String name) {
        if (userService.getById(user.getId()) == null)
            return "redirect:/";

        User userEmail = userService.getUserByEmail(email);
        String errorEmail = "";
        if (!email.matches(User.emailRegex) || (userEmail != null && user.getId() != userEmail.getId())) {
            errorEmail += "&errorEmail=" + true;
        }
        String errorName = "";
        if (name.isEmpty()) {
            errorName += "&errorName=" + true;
        }
        if (!errorEmail.isEmpty() || !errorName.isEmpty())
            return "redirect:/my_profile/edit?" + errorEmail + errorName;

        user.setEmail(email);
        user.setName(name);
        userService.updateUser(user);
        return "redirect:/my_profile";
    }


    @GetMapping("/editPassword")
    public String editPasswordGET(@AuthenticationPrincipal User user,
                                  @RequestParam(required = false, value = "errorCur") Boolean errorCur,
                                  @RequestParam(required = false, value = "errorNew") Boolean errorNew,
                                  Model model) {
        if (userService.getById(user.getId()) == null)
            return "redirect:/";

        if (errorCur != null && errorCur)
            model.addAttribute("errorCur", true);
        if (errorNew != null && errorNew)
            model.addAttribute("errorNew", true);
        model.addAttribute("currentUser", user);
        return "editPassword";
    }

    @PostMapping("/editPassword")
    public String editPasswordPOST(@AuthenticationPrincipal User user,
                                   @RequestParam("curPassword") String curPassword,
                                   @RequestParam("newPassword") String newPassword,
                                   @RequestParam("repPassword") String repPassword) {
        if (userService.getById(user.getId()) == null)
            return "redirect:/";

        if (!passwordEncoder.matches(curPassword, user.getPassword()))
            return "redirect:/my_profile/editPassword?errorCur=" + true;
        if (newPassword.length() > 30 || newPassword.length() < 5 || !newPassword.equals(repPassword))
            return "redirect:/my_profile/editPassword?errorNew=" + true;
        user.setPassword(
                passwordEncoder.encode(newPassword)
        );
        userService.updateUser(user);
        return "redirect:/logout";
    }
}
