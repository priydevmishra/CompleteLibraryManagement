package com.example.LibraryManagement.librarymanagement.Service;

import com.example.LibraryManagement.librarymanagement.Entity.Otp;
import com.example.LibraryManagement.librarymanagement.Entity.User;
import com.example.LibraryManagement.librarymanagement.Repository.OtpRepository;
import com.example.LibraryManagement.librarymanagement.Repository.UserRepository;
import com.example.LibraryManagement.librarymanagement.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        Otp otpObj = new Otp(null, otp, LocalDateTime.now().plusMinutes(5), user);
        return otpRepository.save(otpObj);
    }

    public Boolean verifyOTP(int enteredOtp, String email){
        User user = userRepository.getUserByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User","Email",email));
        if(user.getOtp()!=null && user.getOtp().getValue()==enteredOtp && !user.getOtp().getExpiry().isBefore(LocalDateTime.now())){
            otpRepository.delete(user.getOtp());
            return true;
        }
        return false;
    }

    public Otp resendOTP(String email){
        User user = userRepository.getUserByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User","Email",email));
        Otp existingOtp = user.getOtp();

        int newOtpValue =  new Random().nextInt(9000) +1000;
        LocalDateTime newExpiry = LocalDateTime.now().plusMinutes(5);

        if(existingOtp!=null){
            existingOtp.setValue(newOtpValue);
            existingOtp.setExpiry(newExpiry);
            return otpRepository.save(existingOtp);
        }else{
            Otp otp = new Otp(null,newOtpValue,newExpiry,user);
            return otpRepository.save(otp);
        }
    }
}
