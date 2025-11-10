package com.example.LibraryManagement.librarymanagement.Repository;

import com.example.LibraryManagement.librarymanagement.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
