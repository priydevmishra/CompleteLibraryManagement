package com.example.LibraryManagement.librarymanagement.DTO.ResponseDTO;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class LoginResponseDTO {
    private String message;
    private String jwtToken;
    private String email;
    private Set<String> roles;
}
