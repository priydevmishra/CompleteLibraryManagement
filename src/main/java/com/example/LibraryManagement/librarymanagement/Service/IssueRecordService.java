package com.example.LibraryManagement.librarymanagement.Service;

import com.example.LibraryManagement.librarymanagement.DTO.MessageResponse;
import com.example.LibraryManagement.librarymanagement.DTO.Receipt;
import com.example.LibraryManagement.librarymanagement.DTO.ResponseDTO.IssueRecordResponseDTO;
import com.example.LibraryManagement.librarymanagement.Repository.BookRepository;
import com.example.LibraryManagement.librarymanagement.Repository.IssueRecordRepository;
import com.example.LibraryManagement.librarymanagement.Repository.UserRepository;
import com.example.LibraryManagement.librarymanagement.Entity.Book;
import com.example.LibraryManagement.librarymanagement.Entity.IssueRecord;
import com.example.LibraryManagement.librarymanagement.Entity.User;
import com.example.LibraryManagement.librarymanagement.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
public class IssueRecordService {

    @Autowired
    private IssueRecordRepository issueRecordRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SqsClient sqsClient;

    @Value("${aws.sqs.issue-return-notification-queue-url}")
    private String issueReturnQueueURL;

    private final String libraryName = "Priydev Library";

    @Value("${library.late.finePerDay}")
    private long finePerDay;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${issueRecord.book.returnedDays}")
    private int allowedDays;

    public MessageResponse issueTheBook(Long bookId){
        Book book = bookRepository.findById(bookId).orElseThrow(()->new ResourceNotFoundException("Book","Id",bookId.toString()));
        if(book.getQuantity()<=0 || !book.getAvailable()){
            throw new RuntimeException("Book is not Available");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User oldUser = userRepository.getUserByEmail(email).orElseThrow(()->new ResourceNotFoundException("User","Email",email));

        IssueRecord issueRecord = new IssueRecord();
        issueRecord.setIssueDate(LocalDate.now());
        issueRecord.setDueDate(LocalDate.now().plusDays(allowedDays));
        issueRecord.setIsReturn(false);
        issueRecord.setBook(book);
        issueRecord.setUser(oldUser);

        book.setQuantity(book.getQuantity()-1);
        book.setAvailable(book.getQuantity() > 0);

        Book savedBook = bookRepository.save(book);
        issueRecordRepository.save(issueRecord);
        LocalDateTime timestamp = LocalDateTime.now();
        String now = timestamp.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

        String msg = """
            You have issued the book %s from %s at %s.
            Return within %s days.
            Due date: %s
            """.formatted(savedBook.getTitle(), libraryName, now, allowedDays, issueRecord.getDueDate());

        try{
            Map<String, Object> message = Map.of(
                    "eventType", "ISSUE_BOOK",
                    "email", oldUser.getEmail(),
                    "body", msg
            );
             String json = new ObjectMapper().writeValueAsString(message);
             sqsClient.sendMessage(SendMessageRequest.builder().messageBody(json).queueUrl(issueReturnQueueURL).build());
             return new MessageResponse("Book successfully Issued with title "+savedBook.getTitle(), HttpStatus.OK.value(),LocalDateTime.now(),true);
        }
        catch (Exception e){
            return new MessageResponse("Book successfully Isued with title "+savedBook.getTitle(), HttpStatus.OK.value(),LocalDateTime.now(),true);
        }
    }




    public Map<String, Object> returnTheBook(Long issueRecordId) throws JsonProcessingException {

        IssueRecord issueRecord = issueRecordRepository.findById(issueRecordId).orElseThrow(()-> new ResourceNotFoundException("IssueRecord", "id", issueRecordId.toString()));
        Book book = issueRecord.getBook();

        if(issueRecord.getIsReturn()){
            throw new RuntimeException("Book is Already Returned");
        }

        issueRecord.setIsReturn(true);
        issueRecord.setReturnDate(LocalDate.now());
        book.setQuantity(book.getQuantity()+1);
        book.setAvailable(book.getQuantity() > 0);

        long days=0;
        if(LocalDate.now().isAfter(issueRecord.getDueDate())){
            days = ChronoUnit.DAYS.between(issueRecord.getDueDate(),LocalDate.now());
        }
        long fine = days*finePerDay;
        issueRecord.setLateDays(days);
        issueRecord.setFine(fine);
        issueRecordRepository.save(issueRecord);
        bookRepository.save(book);

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

        String msgBody = """
            You have returned the book %s to %s at %s.
            Late days: %s
            Fine: Rs %s
            """.formatted(
                issueRecord.getBook().getTitle(),
                libraryName,
                now,
                issueRecord.getLateDays(),
                issueRecord.getFine()
                );
        try{
            Map<String, Object> message = Map.of(
                    "eventType","RETURN_BOOK",
                    "email", issueRecord.getUser().getEmail(),
                    "body", msgBody
            );
            String json = new ObjectMapper().writeValueAsString(message);
            sqsClient.sendMessage(SendMessageRequest.builder().messageBody(json).queueUrl(issueReturnQueueURL).build());
            Receipt receipt = modelMapper.map(issueRecord, Receipt.class);
            receipt.setStudentName(issueRecord.getUser().getName());
            receipt.setLibraryName(libraryName);
            receipt.setBookTitle(issueRecord.getBook().getTitle());
            return Map.of("message",msgBody,"receipt",receipt,"success",true);
        }catch (Exception e){
            return Map.of("message","failed to generate receipt","receipt", null,"success", false);
        }
    }



    public Map<String, Object> sendDueReminder(Long issueRecordId) {

        IssueRecord issueRecord = issueRecordRepository.findById(issueRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("IssueRecord", "id", issueRecordId.toString()));

        // Ensure overdueDays is non-negative
        long overdueDays = 0;
        if (issueRecord.getDueDate() != null && LocalDate.now().isAfter(issueRecord.getDueDate())) {
            overdueDays = ChronoUnit.DAYS.between(issueRecord.getDueDate(), LocalDate.now());
        }

        long fineAmount = Math.max(0, overdueDays * finePerDay);

        String msgBody = "Kindly submit the Book " + issueRecord.getBook().getTitle() +
                " to the library. Your charge for the book is Rs " + fineAmount +
                ". Please submit as soon as possible.";

        try {
            Map<String, Object> message = Map.of(
                    "eventType","DUE_REMINDER",
                    "email", issueRecord.getUser().getEmail(),
                    "body", msgBody
            );
            String json = new ObjectMapper().writeValueAsString(message); // use injected mapper
            sqsClient.sendMessage(SendMessageRequest.builder().queueUrl(issueReturnQueueURL).messageBody(json).build());
            return Map.of("message", msgBody, "success", true);
        } catch(Exception e) {
            return Map.of("message", "message did not send, error occured", "success", false);
        }
    }




    public Page<IssueRecordResponseDTO> issueRecordHistory(String keyword, int page, int pageSize, String sortBy, String sortDir){
        Sort sort = sortDir.equalsIgnoreCase("asc")? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,pageSize,sort);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<IssueRecord> pageResult;

        if (keyword == null || keyword.trim().isEmpty()) {
            pageResult = issueRecordRepository.findByUser_Email(email, pageable);
        } else {
            pageResult = issueRecordRepository.findByUser_EmailAndBook_TitleContainingIgnoreCase(
                    email, keyword, pageable
            );
        }

        return pageResult.map(record -> {
            IssueRecordResponseDTO dto = new IssueRecordResponseDTO();

            dto.setId(record.getId());
            dto.setBookTitle(record.getBook().getTitle());
            dto.setIssueDate(record.getIssueDate());
            dto.setDueDate(record.getDueDate());
            dto.setReturnDate(record.getReturnDate());
            dto.setIsReturn(record.getIsReturn());
            dto.setFine(record.getFine());

            return dto;
        });
    }


    public IssueRecordResponseDTO getIssueRecordById(Long id){
        IssueRecord record = issueRecordRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("IssueRecord", "id", id.toString()));
        IssueRecordResponseDTO dto = new IssueRecordResponseDTO();

        dto.setId(record.getId());
        dto.setBookTitle(record.getBook().getTitle());
        dto.setIssueDate(record.getIssueDate());
        dto.setDueDate(record.getDueDate());
        dto.setReturnDate(record.getReturnDate());
        dto.setIsReturn(record.getIsReturn());
        dto.setFine(record.getFine());

        return dto;
    }


    public Page<IssueRecordResponseDTO> getAllActiveRecord(int page, int pageSize, String sortBy, String sortDir){
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,pageSize,sort);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<IssueRecord> pageResult = issueRecordRepository.findByIsReturnFalse(pageable);
        return pageResult.map(record -> {
            IssueRecordResponseDTO dto = new IssueRecordResponseDTO();

            dto.setId(record.getId());
            dto.setBookTitle(record.getBook().getTitle());
            dto.setIssueDate(record.getIssueDate());
            dto.setDueDate(record.getDueDate());
            dto.setReturnDate(record.getReturnDate());
            dto.setIsReturn(record.getIsReturn());
            dto.setFine(record.getFine());

            return dto;
        });
    }


    public Page<IssueRecordResponseDTO> getAllActiveRecordOfUser(int page, int pageSize, String sortBy, String sortDir){
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,pageSize,sort);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<IssueRecord> pageResult = issueRecordRepository.findByUser_EmailAndIsReturnFalse(email,pageable);
        return pageResult.map(record -> {
            IssueRecordResponseDTO dto = new IssueRecordResponseDTO();

            dto.setId(record.getId());
            dto.setBookTitle(record.getBook().getTitle());
            dto.setIssueDate(record.getIssueDate());
            dto.setDueDate(record.getDueDate());
            dto.setReturnDate(record.getReturnDate());
            dto.setIsReturn(record.getIsReturn());
            dto.setFine(record.getFine());

            return dto;
        });
    }


    public Page<IssueRecordResponseDTO> getOverDueIssueRecord(int page, int pageSize, String sortBy, String sortDir){

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,pageSize,sort);

        Page<IssueRecord> pageResult = issueRecordRepository.findByIsReturnFalseAndDueDateBefore(LocalDate.now(),pageable);
        return pageResult.map(record -> {
            IssueRecordResponseDTO dto = new IssueRecordResponseDTO();

            dto.setId(record.getId());
            dto.setBookTitle(record.getBook().getTitle());
            dto.setIssueDate(record.getIssueDate());
            dto.setDueDate(record.getDueDate());
            dto.setReturnDate(record.getReturnDate());
            dto.setIsReturn(record.getIsReturn());
            dto.setFine(record.getFine());

            return dto;
        });
    }


    public Page<IssueRecordResponseDTO> getBookIssueRecord(Long booId, int page, int pageSize, String sortBy, String sortDir){

        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending():Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page,pageSize,sort);

        Page<IssueRecord> pageResult = issueRecordRepository.findByBook_Id(booId,pageable);
        return pageResult.map(record -> {
            IssueRecordResponseDTO dto = new IssueRecordResponseDTO();

            dto.setId(record.getId());
            dto.setBookTitle(record.getBook().getTitle());
            dto.setIssueDate(record.getIssueDate());
            dto.setDueDate(record.getDueDate());
            dto.setReturnDate(record.getReturnDate());
            dto.setIsReturn(record.getIsReturn());
            dto.setFine(record.getFine());

            return dto;
        });
    }
}
