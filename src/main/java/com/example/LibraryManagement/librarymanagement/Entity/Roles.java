package com.example.LibraryManagement.librarymanagement.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "roles")
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
