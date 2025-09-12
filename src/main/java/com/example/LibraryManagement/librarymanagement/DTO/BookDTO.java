package com.example.LibraryManagement.librarymanagement.DTO;

import lombok.Data;

@Data
public class BookDTO {
    private String title;
    private String authorName;
    private String isbn;
    private int quantity;
    private Boolean available;
}
