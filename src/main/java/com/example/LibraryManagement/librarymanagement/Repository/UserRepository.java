package com.example.LibraryManagement.librarymanagement.Repository;

import com.example.LibraryManagement.librarymanagement.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> getUserByEmail(String email);
}
