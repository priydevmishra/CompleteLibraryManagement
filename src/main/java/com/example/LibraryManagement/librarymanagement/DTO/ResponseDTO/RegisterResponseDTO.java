package com.example.LibraryManagement.librarymanagement.DTO.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponseDTO {
    private String message;
    private Long id;
    private String email;
}
