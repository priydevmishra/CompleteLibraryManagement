package com.example.LibraryManagement.librarymanagement.Controller;

import com.example.LibraryManagement.librarymanagement.DTO.BookDTO;
import com.example.LibraryManagement.librarymanagement.DTO.ResponseDTO.SavedBookResponseDTO;
import com.example.LibraryManagement.librarymanagement.Entity.Book;
import com.example.LibraryManagement.librarymanagement.Service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping()
    public ResponseEntity<List<Book>> getAllBooks(){
       return ResponseEntity.status(HttpStatus.OK).body(bookService.getAllBooks());
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id){
        BookDTO bookDTO = bookService.getBookById(id);
        return ResponseEntity.status(HttpStatus.FOUND).body(bookDTO);
    }

    @PostMapping("/addBook")
    public ResponseEntity<SavedBookResponseDTO> addBook(@RequestBody BookDTO bookDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(bookDTO));
    }

    @PutMapping("/update-book/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SavedBookResponseDTO> updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.updateBook(id, bookDTO));
    }

    @DeleteMapping("/delete-book/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SavedBookResponseDTO> deleteBook(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.deleteBook(id));
    }
}
