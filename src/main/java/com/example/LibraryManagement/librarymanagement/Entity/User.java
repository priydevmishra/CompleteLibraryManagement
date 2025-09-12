package com.example.LibraryManagement.librarymanagement.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Entity
@Data
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String mobileNumber;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)  // isse ek seprate table ban jaayegi database me jiska name hoga, users_roles one column is id of user
    private Set<String> roles;  //and second is collection of roles of users. Aur ye collection fully owned by User entity. Means bina User ke element exixt nhi karte.
}
