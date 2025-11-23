package com.example.LibraryManagement.librarymanagement.exception;

import com.example.LibraryManagement.librarymanagement.DTO.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> resourceNotFoundExceptionHandler(Exception ex){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setDateTime(LocalDateTime.now());
        apiResponse.setError("Resource Not Found");
        apiResponse.setStatus((short) HttpStatus.NOT_FOUND.value());
        apiResponse.setMessage("The Requested Entity does not exist : "+ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

//    @ExceptionHandler(RateLimitExceedException.class)
//    public ResponseEntity<ApiResponse> rateLimitExceedExceptionHandler(IllegalStateException ex){
//        ApiResponse apiResponse = new ApiResponse();
//        apiResponse.setStatus((short) HttpStatus.NOT_ACCEPTABLE.value());
//        apiResponse.setMessage("Please wait until new OTP time is not Started");
//        apiResponse.setError("Reques");
//
//    }
}
