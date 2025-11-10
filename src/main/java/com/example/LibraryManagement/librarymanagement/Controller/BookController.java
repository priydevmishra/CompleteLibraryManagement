package com.example.LibraryManagement.librarymanagement.Controller;

import com.example.LibraryManagement.librarymanagement.DTO.BookDTO;
import com.example.LibraryManagement.librarymanagement.Entity.Book;
import com.example.LibraryManagement.librarymanagement.Service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping()
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/AllBooks")
    public ResponseEntity<List<Book>> getAllBooks(){
       return ResponseEntity.status(HttpStatus.OK).body(bookService.getAllBooks());
    }

    @GetMapping("/book/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id){
        Book book = bookService.getBookById(id);
        if(book!=null){
            return ResponseEntity.status(HttpStatus.OK).body(book);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addBook")
    public ResponseEntity<Book> addBook(@RequestBody BookDTO bookDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(bookDTO));
    }

    @PutMapping("/update-book/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.updateBook(id, bookDTO));
    }

    @DeleteMapping("/delete-book/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id){
        return ResponseEntity.noContent().build();
    }
}
