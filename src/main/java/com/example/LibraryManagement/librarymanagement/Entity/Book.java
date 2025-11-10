package com.example.LibraryManagement.librarymanagement.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Entity
@Data
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String authorName;
    private String isbn;
    private int quantity;
    private Boolean available;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<IssueRecord> issueRecords;
}
