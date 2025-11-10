package com.example.LibraryManagement.librarymanagement.Repository;

import com.example.LibraryManagement.librarymanagement.Entity.IssueRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueRecordRepository extends JpaRepository<IssueRecord, Long> {

}
