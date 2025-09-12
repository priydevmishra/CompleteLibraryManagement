package com.example.LibraryManagement.librarymanagement.DTO.ResponseDTO;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class LoginResponseDTO {
    private String jwtToken;  //mendatory
    private String email;   //optional
    private Set<String> roles;  //optional

}
