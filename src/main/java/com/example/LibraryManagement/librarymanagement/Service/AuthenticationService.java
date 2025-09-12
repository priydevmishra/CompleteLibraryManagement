package com.example.LibraryManagement.librarymanagement.Service;

import com.example.LibraryManagement.librarymanagement.BookRepository.UserRepository;
import com.example.LibraryManagement.librarymanagement.DTO.RequestDTO.LoginRequestDTO;
import com.example.LibraryManagement.librarymanagement.DTO.RequestDTO.RegisterRequestDTO;
import com.example.LibraryManagement.librarymanagement.DTO.ResponseDTO.LoginResponseDTO;
import com.example.LibraryManagement.librarymanagement.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
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
    
    public User registerNormalUser(RegisterRequestDTO registerRequestDTO){

        if(userRepository.getUserByEmail(registerRequestDTO.getEmail()).isPresent()){
            throw new RuntimeException("User Already Registered...");
        }

        Set<String> roles = new HashSet<String>();
        roles.add("ROLE_USER");

        User user = new User();
        user.setName(registerRequestDTO.getName());
        user.setEmail(registerRequestDTO.getEmail());
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));

        return userRepository.save(user);
    }

    public User registerAdminUser(RegisterRequestDTO registerRequestDTO){

        if(userRepository.getUserByEmail(registerRequestDTO.getEmail()).isPresent()){
            throw new RuntimeException("User already Exist");
        }

        Set<String> roles = new HashSet<String>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_USER");

        User user = new User();
        user.setName(registerRequestDTO.getName());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword())
        );

        User user = userRepository.getUserByEmail(loginRequestDTO.getEmail()).orElseThrow(()->new RuntimeException("UserNot Found"));

        String jwtToken = jwtService.generateToken(user);
        return LoginResponseDTO.builder().jwtToken(jwtToken).email(user.getEmail()).roles(user.getRoles()).build();
    }


}
