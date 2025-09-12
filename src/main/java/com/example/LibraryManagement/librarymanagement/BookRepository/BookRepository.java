package com.example.LibraryManagement.librarymanagement.BookRepository;

import com.example.LibraryManagement.librarymanagement.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
