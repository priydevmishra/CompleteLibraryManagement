package com.example.LibraryManagement.librarymanagement.Controller;

import com.example.LibraryManagement.librarymanagement.DTO.MessageResponse;
import com.example.LibraryManagement.librarymanagement.DTO.ResponseDTO.IssueRecordResponseDTO;
import com.example.LibraryManagement.librarymanagement.Service.IssueRecordService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/issuerecord")
public class IssueRecordController {

    @Autowired
    private IssueRecordService issueRecordService;

    @PostMapping("/issuethebook/{bookId}")
    public ResponseEntity<MessageResponse> issueTheBook(@PathVariable Long bookId){
        return ResponseEntity.status(HttpStatus.OK).body(issueRecordService.issueTheBook(bookId));
    }

    @PostMapping("/returnthebook/{issueRecordId}")
    public ResponseEntity<Map<String,Object>> returnTheBook(@PathVariable Long issueRecordId) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.OK).body(issueRecordService.returnTheBook(issueRecordId));
    }

    @GetMapping("/history")
    public ResponseEntity<Page<IssueRecordResponseDTO>> issueRecordHistory(@RequestParam(defaultValue = "") String keyword, @RequestParam(defaultValue= "0") int page, @RequestParam(defaultValue = "8") int pageSize, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir){
        Page<IssueRecordResponseDTO> pageContent = issueRecordService.issueRecordHistory(keyword,page,pageSize,sortBy,sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(pageContent);
    }

    @GetMapping("{id}")
    public ResponseEntity<IssueRecordResponseDTO> getIssueRecordById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(issueRecordService.getIssueRecordById(id));
    }

    @GetMapping("/active")
    public ResponseEntity<Page<IssueRecordResponseDTO>> getActiveRecord(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int pageSize, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir){
        return ResponseEntity.status(HttpStatus.OK).body(issueRecordService.getAllActiveRecord(page,pageSize,sortBy,sortDir));
    }

    @GetMapping("/active-user")
    public ResponseEntity<Page<IssueRecordResponseDTO>> getActiveRecordOfUser(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int pageSize, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir){
        return ResponseEntity.status(HttpStatus.OK).body(issueRecordService.getAllActiveRecordOfUser(page,pageSize,sortBy,sortDir));
    }

    @GetMapping("/overdue")
    public ResponseEntity<Page<IssueRecordResponseDTO>> getOverDueIssueRecord(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int pageSize, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir){
        return ResponseEntity.status(HttpStatus.OK).body(issueRecordService.getOverDueIssueRecord(page,pageSize,sortBy,sortDir));
    }

    @PostMapping("/send-reminder")
    public ResponseEntity<Map<String,Object>> sendDueReminder(@RequestParam Long issueRecordId){
        return ResponseEntity.ok(issueRecordService.sendDueReminder(issueRecordId));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<Page<IssueRecordResponseDTO>> getBookIssueRecord(@PathVariable Long bookId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int pageSize, @RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String sortDir){
        return ResponseEntity.status(HttpStatus.OK).body(issueRecordService.getBookIssueRecord(bookId,page,pageSize,sortBy,sortDir));
    }
}


// Ye 3 api bnaani hain baad me..


// 1. Admin-Only Filters
// 2. Bulk Due Reminder Trigger
// 3. Admin Resend Return Receipt