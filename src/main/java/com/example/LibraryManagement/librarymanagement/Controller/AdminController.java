package com.example.LibraryManagement.librarymanagement.Controller;

import com.example.LibraryManagement.librarymanagement.DTO.RequestDTO.RegisterRequestDTO;
import com.example.LibraryManagement.librarymanagement.DTO.ResponseDTO.RegisterResponseDTO;
import com.example.LibraryManagement.librarymanagement.Entity.User;
import com.example.LibraryManagement.librarymanagement.Service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register-admin")
    public ResponseEntity<RegisterResponseDTO> registerAdminUser(@RequestBody RegisterRequestDTO registerRequestDTO){
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.registerAdminUser(registerRequestDTO));
    }

    //kewal registration ke liye hume alag-alag controller bnaane pade hain, hum login(Authenticate) same method se kraa sakte hain.
    // jab kisi company ke software me pehla admin register karna hota hai, to hum use sql script se register karte hain.
    // Aur fir wo admin as a admin login karta hai, aur is registeradmin api ko access karta hai. koi public method nhi hota.
}
