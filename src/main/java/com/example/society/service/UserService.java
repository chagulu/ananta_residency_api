package com.example.society.service;

import com.example.society.dto.UserDto;
import com.example.society.model.Guard;
import com.example.society.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void registerUser(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        Guard user = new Guard();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword()); // NOTE: In production, always hash the password!

        userRepository.save(user);
    }
}
