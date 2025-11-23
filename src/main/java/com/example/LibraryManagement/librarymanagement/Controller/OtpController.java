package com.example.LibraryManagement.librarymanagement.Controller;

import com.example.LibraryManagement.librarymanagement.DTO.EmailRequestDTO;
import com.example.LibraryManagement.librarymanagement.DTO.MessageResponse;
import com.example.LibraryManagement.librarymanagement.DTO.RequestDTO.VerifyOtpRequestDTO;
import com.example.LibraryManagement.librarymanagement.Entity.Otp;
import com.example.LibraryManagement.librarymanagement.Service.OtpService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/generate-otp")
    public ResponseEntity<MessageResponse> generateOTP(@RequestBody EmailRequestDTO emailRequestDTO) throws JsonProcessingException {
        MessageResponse response = otpService.generateOTP(emailRequestDTO.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Boolean> verifyOTP(@RequestBody VerifyOtpRequestDTO verifyOtpRequestDTO){
        return ResponseEntity.status(HttpStatus.OK).body(otpService.verifyOTP(verifyOtpRequestDTO.getOtp(),verifyOtpRequestDTO.getEmail()));
    }

}
