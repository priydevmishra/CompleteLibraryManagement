package com.example.LibraryManagement.librarymanagement.Repository;

import com.example.LibraryManagement.librarymanagement.Entity.IssueRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;


public interface IssueRecordRepository extends JpaRepository<IssueRecord, Long> {
    Page<IssueRecord> findByUser_EmailAndBook_TitleContainingIgnoreCase(String email, String keyword, Pageable pageable); // Parent_childAttribute ko follow karo
    Page<IssueRecord> findByUser_Email(String email, Pageable pageable);
    Page<IssueRecord> findByUser_EmailAndIsReturnFalse(String email, Pageable pageable);
    Page<IssueRecord> findByIsReturnFalse(Pageable pageable);
    Page<IssueRecord> findByIsReturnFalseAndDueDateBefore(LocalDate date, Pageable pageable);
    Page<IssueRecord> findByBook_Id(Long bookId, Pageable pageable);
}
