package com.example.LibraryManagement.librarymanagement.Service;

import com.example.LibraryManagement.librarymanagement.Entity.Otp;
import com.example.LibraryManagement.librarymanagement.Entity.User;
import com.example.LibraryManagement.librarymanagement.Repository.OtpRepository;
import com.example.LibraryManagement.librarymanagement.Repository.UserRepository;
import com.example.LibraryManagement.librarymanagement.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private UserRepository userRepository;

    public Otp generateOTP(String email){
        int otp = new Random().nextInt(9000) + 1000;
        User user = userRepository.getUserByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User","Email",email));

        LocalDateTime now = LocalDateTime.now();

        if(user.getTotalAttempts()>5){
            if(user.getLastOtpSent()!=null && user.getLastOtpSent().plusMinutes(5).isAfter(now)){
                throw new IllegalStateException("Too many attempts... please try again later..");
            }else{
                user.setSentOtpCount(0);
            }
        }

        if(user.getLastOtpSent()!=null && user.getLastOtpSent().plusSeconds(5).isAfter(now)){
            throw new IllegalStateException("Please wait before requesting another OTP.");
        }

        if(user.getLastOtpSent()!=null && !user.getLastOtpSent().toLocalDate().isEqual(now.toLocalDate())){
            user.setSentOtpCount(0);
        }

        if(user.getSentOtpCount()>8){
            throw new IllegalStateException("Daily OTP limited reached... Please try Again tomorrow..");
        }

        if (user.getOtp() != null) {
            otpRepository.delete(user.getOtp());
            otpRepository.flush();
            user.setOtp(null);
            userRepository.save(user);
        }

        int otpValue = new Random().nextInt(9000)+1000;
        Otp otpObj = new Otp(null, otpValue, now.plusMinutes(5), user);
        user.setOtp(otpObj);
        otpRepository.save(otpObj);

        user.setSentOtpCount(user.getSentOtpCount()+1);
        user.setLastOtpSent(now);

        userRepository.save(user);

        return user.getOtp();
    }

    public Boolean verifyOTP(int enteredOtp, String email){
        User user = userRepository.getUserByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User","Email",email));

        if(user.getOtp()==null){
            return false;
        }

        if(user.getTotalAttempts()>8){
            return false;
        }

        boolean isValid = user.getOtp().getValue()==enteredOtp && !user.getOtp().getExpiry().isBefore(LocalDateTime.now());
        if(isValid){
            otpRepository.delete(user.getOtp());
            user.setOtp(null);
            user.setTotalAttempts(0);
            userRepository.save(user);
            return true;
        }

        user.setTotalAttempts(user.getTotalAttempts()+1);
        userRepository.save(user);
        return false;
    }

}
