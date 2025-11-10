package com.example.LibraryManagement.librarymanagement.Service;

import com.example.LibraryManagement.librarymanagement.DTO.ResponseDTO.RegisterResponseDTO;
import com.example.LibraryManagement.librarymanagement.Entity.User;
import com.example.LibraryManagement.librarymanagement.Repository.UserRepository;
import com.example.LibraryManagement.librarymanagement.DTO.RequestDTO.LoginRequestDTO;
import com.example.LibraryManagement.librarymanagement.DTO.RequestDTO.RegisterRequestDTO;
import com.example.LibraryManagement.librarymanagement.DTO.ResponseDTO.LoginResponseDTO;
import com.example.LibraryManagement.librarymanagement.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;  // spring gives these two Classes by default

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;
    
    public RegisterResponseDTO registerNormalUser(RegisterRequestDTO registerRequestDTO){

        if(userRepository.getUserByEmail(registerRequestDTO.getEmail()).isPresent()){
            throw new RuntimeException("User Already Registered...");
        }

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");

        User user = new User();
        user.setName(registerRequestDTO.getName());
        user.setEmail(registerRequestDTO.getEmail());
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        User savedUser = userRepository.save(user);

        return new RegisterResponseDTO("User successfully Registered with email "+savedUser.getEmail(),savedUser.getId(),savedUser.getEmail());
    }

    public RegisterResponseDTO registerAdminUser(RegisterRequestDTO registerRequestDTO){

        if(userRepository.getUserByEmail(registerRequestDTO.getEmail()).isPresent()){
            throw new RuntimeException("User already Exist");
        }

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_USER");

        User user = new User();
        user.setName(registerRequestDTO.getName());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setRoles(roles);
        User savedAdmin = userRepository.save(user);

        return new RegisterResponseDTO("Admin successfully Registered with email "+savedAdmin.getEmail(),savedAdmin.getId(),savedAdmin.getEmail());
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword())
        );

        User user = (User) userRepository.getUserByEmail(loginRequestDTO.getEmail()).orElseThrow(()->new ResourceNotFoundException("User","Email",loginRequestDTO.getEmail()));

        String jwtToken = jwtService.generateToken(user.getEmail());
        return LoginResponseDTO.builder().jwtToken(jwtToken).email(user.getEmail()).roles(user.getRoles()).message("You are Successfully Logged In").build();
    }
}
