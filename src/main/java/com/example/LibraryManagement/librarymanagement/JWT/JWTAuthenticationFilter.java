package com.example.LibraryManagement.librarymanagement.JWT;

import com.example.LibraryManagement.librarymanagement.BookRepository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {  //OnceperRF kaa use basically async ke one request ke liye verify it.
    //this is the main class, jwt service is the implementation of the methods used in this class
    //Steps :-
    //i). getHeader which will be in the form of Bearer abcd.efgh.ijkl  // header.Claims.signature(secretKey)
    //ii). we store bearer in a variable like s=s.substring(7)  //bearer length is 7.
    //iii). Extract the username (btw payload contains username and otherdetails)
    //iv). Get the username by using username using derived query method.
    //v). validate the token.
    //vi). Load the user and create the authentication with userRoles
    //vii). Set the authentication

    @Autowired
    private final JWTService jwtService;

    @Autowired
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                                        throws ServletException, IOException {
        final String authheader = request.getHeader("Authorization");  // jo request aa rhi hai, usme header bhi aayega.
        final String jwtToken;
        final String email;

        // Check kro ki Authentication header present hai, yaa start ho rhaa hai Bearer se.
        if(authheader==null || !authheader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        // Extract JWT Token from header
        jwtToken = authheader.substring(7);
        email = jwtService.extractEmail(jwtToken);

        //check if we have a email and no authentication exist yet. it is because
        //, we dont want to reauthenticate because to save time.
        if(email!=null&& SecurityContextHolder.getContext().getAuthentication()==null){
            //get the user details from database
            var userDetails = userRepository.getUserByEmail(email).orElseThrow(()-> new RuntimeException("User not Found"));

            //validate the token
            if(jwtService.isTokenValid(jwtToken, userDetails)){
                //create the Authentication with user roles
                List<SimpleGrantedAuthority> authorities = userDetails.getRoles().stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());

                // create authentication with username
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,null,authorities
                );

                //Set authentication details
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // update the security context
                SecurityContextHolder.getContext().setAuthentication(authToken);

            }
        }
        filterChain.doFilter(request,response);
    }
}
