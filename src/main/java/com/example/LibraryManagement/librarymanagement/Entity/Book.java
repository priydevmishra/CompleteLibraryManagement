package com.example.LibraryManagement.librarymanagement.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String title;
    private String authorName;
    private String isbn;
    private int quantity;
    private Boolean available;
}
