package com.example.LibraryManagement.librarymanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private String message;
    private short status;
    private LocalDateTime dateTime;
    private String error;
}
