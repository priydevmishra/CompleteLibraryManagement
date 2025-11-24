package com.example.LibraryManagement.librarymanagement.Service;

import com.example.LibraryManagement.librarymanagement.DTO.ResponseDTO.SavedBookResponseDTO;
import com.example.LibraryManagement.librarymanagement.Repository.BookRepository;
import com.example.LibraryManagement.librarymanagement.DTO.BookDTO;
import com.example.LibraryManagement.librarymanagement.Entity.Book;
import com.example.LibraryManagement.librarymanagement.exception.BadRequestException;
import com.example.LibraryManagement.librarymanagement.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }

    public BookDTO getBookById(Long id){
        Book searchedBook = bookRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Book","Id",id.toString()));
        return modelMapper.map(searchedBook,BookDTO.class);
    }

    public SavedBookResponseDTO createBook(BookDTO bookDTO){

       Book book = modelMapper.map(bookDTO,Book.class);
       Book savedBook = bookRepository.save(book);

       return new SavedBookResponseDTO("Book successfully saved with name : "+savedBook.getTitle(),savedBook.getTitle(),savedBook.getAuthorName(),true);
    }

    public SavedBookResponseDTO updateBook(Long id, BookDTO bookDTO){
        Book oldBook = bookRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Book","Id",id.toString()));
        modelMapper.map(bookDTO,oldBook);
        Book savedBook = bookRepository.save(oldBook);
        return new SavedBookResponseDTO("Book successfully updated with name : "+savedBook.getTitle(),savedBook.getTitle(),savedBook.getAuthorName(),true);
    }

    public SavedBookResponseDTO deleteBook(Long id){
        Book book = bookRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Book","Id",id.toString()));
        bookRepository.delete(book);
        return new SavedBookResponseDTO("Book successfully Deleted with name : "+book.getTitle(),book.getTitle(),book.getAuthorName(),true);
    }
}
