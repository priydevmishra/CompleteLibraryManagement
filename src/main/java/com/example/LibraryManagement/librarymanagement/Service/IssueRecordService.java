package com.example.LibraryManagement.librarymanagement.Service;

import com.example.LibraryManagement.librarymanagement.BookRepository.BookRepository;
import com.example.LibraryManagement.librarymanagement.BookRepository.IssueRecordRepository;
import com.example.LibraryManagement.librarymanagement.BookRepository.UserRepository;
import com.example.LibraryManagement.librarymanagement.Entity.Book;
import com.example.LibraryManagement.librarymanagement.Entity.IssueRecord;
import com.example.LibraryManagement.librarymanagement.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class IssueRecordService {

    @Autowired
    private IssueRecordRepository issueRecordRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    public IssueRecord issueTheBook(Long bookId){
        Book book = bookRepository.findById(bookId).orElseThrow(()->new RuntimeException("Book not Found"));
        if(book.getQuantity()<=0 || !book.getAvailable()){
            throw new RuntimeException("Book is not Available");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<User> oldUser = userRepository.getUserByEmail(email);
        User user = null;
        if(oldUser.isPresent()){
            user = oldUser.get();
        }else{
            throw new RuntimeException("User Cannot Found with Email "+email);
        }

        IssueRecord issueRecord = new IssueRecord();
        issueRecord.setIssueDate(LocalDate.now());
        issueRecord.setDueDate(LocalDate.now().plusDays(14));
        issueRecord.setIsReturn(false);
        issueRecord.setBook(book);
        issueRecord.setUser(user);

        book.setQuantity(book.getQuantity()-1);
        if(book.getQuantity()==0){
            book.setAvailable(false);
        }

        bookRepository.save(book);
        return issueRecordRepository.save(issueRecord);
    }

    public IssueRecord returnTheBook(Long issueRecordId){

        IssueRecord issueRecord = issueRecordRepository.findById(issueRecordId).orElseThrow(()->new RuntimeException("Issue Record Not Found"));
        Book book = issueRecord.getBook();

        if(issueRecord.getIsReturn()){
            throw new RuntimeException("Book is Already Returned");
        }

        issueRecord.setIsReturn(true);
        issueRecord.setReturnDate(LocalDate.now());

        book.setAvailable(true);
        book.setQuantity(book.getQuantity()+1);
        return issueRecordRepository.save(issueRecord);
    }
}
