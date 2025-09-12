package com.example.LibraryManagement.librarymanagement.Controller;

import com.example.LibraryManagement.librarymanagement.Entity.IssueRecord;
import com.example.LibraryManagement.librarymanagement.Service.IssueRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/issuerecords")
public class IssueRecordController {

    @Autowired
    private IssueRecordService issueRecordService;

    @PostMapping("/issuethebook/{bookId}")
    public ResponseEntity<IssueRecord> issueTheBook(@PathVariable Long bookId){
        return ResponseEntity.status(HttpStatus.OK).body(issueRecordService.issueTheBook(bookId));
    }

    @PostMapping("/returnthebook/{issueRecordId}")
    public ResponseEntity<IssueRecord> returnTheBook(@PathVariable Long issueRecordId){
        return ResponseEntity.status(HttpStatus.OK).body(issueRecordService.returnTheBook(issueRecordId));
    }
}
