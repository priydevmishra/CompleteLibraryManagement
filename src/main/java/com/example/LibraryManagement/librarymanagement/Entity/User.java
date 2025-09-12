package com.example.LibraryManagement.librarymanagement.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@Table(name="users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String mobileNumber;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)  // isse ek seprate table ban jaayegi database me jiska name hoga, users_roles one column is id of user
    private Set<String> roles;  //and second is collection of roles of users. Aur ye collection fully owned by User entity. Means bina User ke element exixt nhi karte.

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role->new SimpleGrantedAuthority(role)).collect(Collectors.toList());
    }
}
