package com.example.springboot.validator;

import com.example.springboot.entity.User;
import com.example.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    private final UserService userService;

    @Autowired
    public UserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        User userEmail = userService.getUserByEmail(user.getEmail());
        if (userEmail != null && user.getId() != userEmail.getId())
            errors.rejectValue("email", "", "User with this email already exist");

        if (user.getName().isEmpty())
            errors.rejectValue("name", "","Name is required");

        if (user.getEmail().isEmpty())
            errors.rejectValue("email", "","Email is required");

        if (user.getPassword().length() < 5 || user.getPassword().length() > 30)
            errors.rejectValue("password", "","Password must be from 5 to 30 symbols");
    }
}
