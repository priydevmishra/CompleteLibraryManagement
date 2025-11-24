package com.example.LibraryManagement.librarymanagement.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Receipt {
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private Boolean isReturn;
    private long fine;
    private long lateDays;
    private String studentName;
    private String libraryName;
    private String bookTitle;
}
