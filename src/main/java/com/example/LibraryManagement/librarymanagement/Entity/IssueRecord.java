package com.example.LibraryManagement.librarymanagement.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "issue_record")
public class IssueRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Boolean isReturn;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
}
