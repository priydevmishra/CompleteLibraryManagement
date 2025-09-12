package com.example.LibraryManagement.librarymanagement.Service;

import com.example.LibraryManagement.librarymanagement.BookRepository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.getUserByEmail(email).orElseThrow(()->new RuntimeException("User Not Found..."));
    }
}  // this method used at the time of initial Login
