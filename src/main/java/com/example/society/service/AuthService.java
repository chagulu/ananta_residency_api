package com.example.society.service;

import com.example.society.dto.ResidenceRegisterRequest;
import com.example.society.global.exception.UserAlreadyExistsException;
import com.example.society.model.Residence;
import com.example.society.model.Guard;
import com.example.society.repository.ResidenceRepository;
import com.example.society.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ResidenceRepository residenceRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, ResidenceRepository residenceRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.residenceRepository = residenceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Residence registerResident(ResidenceRegisterRequest request) {
        // --- 1. Validate for existing user by mobile number ---
        if (userRepository.findByMobileNo(request.getMobileNo()).isPresent()) {
            throw new UserAlreadyExistsException("User with this mobile number already exists.");
        }

        // --- 2. Determine and Validate Username (derived from residenceName) ---
        String usernameForUser = request.getResidenceName();

        if (usernameForUser == null || usernameForUser.trim().isEmpty()) {
            throw new UserAlreadyExistsException("Resident name (username) cannot be empty.");
        }
        if (userRepository.findByUsername(usernameForUser).isPresent()) {
            throw new UserAlreadyExistsException("Username '" + usernameForUser + "' is already taken. Please choose a different name.");
        }

        // --- 3. Create and Save the User ---
        Guard user = new Guard();
        user.setUsername(usernameForUser);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setMobileNo(request.getMobileNo());
        user.setEmail(request.getEmail());
        userRepository.save(user);

        // --- 4. Create and Save the Residence ---
        Residence residence = new Residence();
        residence.setName(request.getResidenceName());
        residence.setMobileNo(request.getMobileNo());
        residence.setBuildingNumber(request.getBuildingNumber());
        residence.setFlatNumber(request.getFlatNumber());
        residence.setTotalMembers(request.getTotalMembers());
        residence.setVehicleDetails(request.getVehicleDetails());

        residence.setAddress(request.getAddress());
        residence.setCity(request.getCity());
        residence.setState(request.getState());
        // REMOVED: residence.setPincode(request.getPincode()); // <--- REMOVE THIS LINE

        residence.setCreatedBy(user);

        return residenceRepository.save(residence);
    }
}