package com.example.LibraryManagement.librarymanagement.JWT;

import com.example.LibraryManagement.librarymanagement.Entity.User;
import com.example.LibraryManagement.librarymanagement.Repository.UserRepository;
import com.example.LibraryManagement.librarymanagement.Service.CustomUserDetailsService;
import com.example.LibraryManagement.librarymanagement.Service.JWTService;
import com.example.LibraryManagement.librarymanagement.exception.ResourceNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;
    //OnceperRF kaa use basically async ke one request ke liye verify it.
    //this is the main class, jwt service is the implementation of the methods used in this class
    //Steps :-
    //i). getHeader which will be in the form of Bearer abcd.efgh.ijkl  // header.Claims.signature(secretKey)
    //ii). we store bearer in a variable like s=s.substring(7)  //bearer length is 7.
    //iii). Extract the username (btw payload contains username and otherdetails)
    //iv). Get the username by using username using derived query method.
    //v). validate the token.
    //vi). Load the user and create the authentication with userRoles
    //vii). Set the authentication

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String email;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = authHeader.substring(7);

        try {
            email = jwtService.extractEmail(jwtToken);
        } catch (Exception e) {
            // token invalid ya expired hai, ignore and move on
            filterChain.doFilter(request, response);
            return;
        }


        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (userDetails!=null && jwtService.isTokenValid(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
