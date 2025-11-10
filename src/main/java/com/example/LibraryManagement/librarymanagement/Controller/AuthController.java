package com.example.LibraryManagement.librarymanagement.Controller;

import com.example.LibraryManagement.librarymanagement.DTO.RequestDTO.LoginRequestDTO;
import com.example.LibraryManagement.librarymanagement.DTO.RequestDTO.RegisterRequestDTO;
import com.example.LibraryManagement.librarymanagement.DTO.ResponseDTO.LoginResponseDTO;
import com.example.LibraryManagement.librarymanagement.DTO.ResponseDTO.RegisterResponseDTO;
import com.example.LibraryManagement.librarymanagement.Entity.User;
import com.example.LibraryManagement.librarymanagement.Service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register-user")
    public ResponseEntity<RegisterResponseDTO> registerNormalUser(@RequestBody RegisterRequestDTO registerRequestDTO){
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.registerNormalUser(registerRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO){
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.login(loginRequestDTO));
    }
}
