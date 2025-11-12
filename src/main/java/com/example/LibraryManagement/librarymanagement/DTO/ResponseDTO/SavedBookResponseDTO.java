package com.example.LibraryManagement.librarymanagement.DTO.ResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SavedBookResponseDTO {
    private String message;
    private String title;
    private String AuthorName;
}
