package com.example.LibraryManagement.librarymanagement.Service;

import com.example.LibraryManagement.librarymanagement.Repository.BookRepository;
import com.example.LibraryManagement.librarymanagement.DTO.BookDTO;
import com.example.LibraryManagement.librarymanagement.Entity.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }

    public Book getBookById(Long id){
        Book book = bookRepository.findById(id).orElseThrow(()-> new RuntimeException("Book Not Found with the Id : "+id));
        return book;
    }

    public Book createBook(BookDTO bookDTO){
       Book book = new Book();
       book.setTitle(bookDTO.getTitle());
       book.setAuthorName(bookDTO.getAuthorName());
       book.setIsbn(bookDTO.getIsbn());
       book.setQuantity(bookDTO.getQuantity());
       book.setAvailable(bookDTO.getAvailable());

       return bookRepository.save(book);
    }

    public Book updateBook(Long id, BookDTO bookDTO){
        Book oldBook = bookRepository.findById(id).orElseThrow(()->new RuntimeException("Book Not Found"));

        oldBook.setTitle(bookDTO.getTitle());
        oldBook.setAuthorName(bookDTO.getAuthorName());
        oldBook.setIsbn(bookDTO.getIsbn());
        oldBook.setQuantity(bookDTO.getQuantity());
        oldBook.setAvailable(bookDTO.getAvailable());

        return bookRepository.save(oldBook);
    }

    public void deleteBook(Long id){
        bookRepository.deleteById(id);
    }
}
