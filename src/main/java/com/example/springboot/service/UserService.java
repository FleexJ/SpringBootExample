package com.example.springboot.service;

import com.example.springboot.entity.User;
import com.example.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(int id) {
        User user = null;
        try {
            user = userRepository.getOne(id);
        } catch (EntityNotFoundException ignored) {}
        return user;
    }

    public User getUserByEmail(String email) {
        User user = null;
        try {
            user = userRepository.getFirstByEmail(email);
        } catch (EntityNotFoundException ignored) {}
        return user;
    }

    @Transactional
    public void addUser(User user) {
        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(User user) {
        try {
            userRepository.save(user);
        } catch (EntityNotFoundException ignored) {}
    }

    @Transactional
    public void deleteById(int id) {
        try {
            userRepository.deleteById(id);
        } catch (EntityNotFoundException ignored) {}
    }
}
