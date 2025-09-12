package com.example.LibraryManagement.librarymanagement.BookRepository;

import com.example.LibraryManagement.librarymanagement.Entity.IssueRecord;
import com.example.LibraryManagement.librarymanagement.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRecordRepository extends JpaRepository<IssueRecord, Long> {

}
