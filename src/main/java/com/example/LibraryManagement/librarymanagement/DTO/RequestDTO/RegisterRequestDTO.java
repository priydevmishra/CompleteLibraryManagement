package com.example.LibraryManagement.librarymanagement.DTO.RequestDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequestDTO {

    private String name;
    private String email;
    private String password;
}
