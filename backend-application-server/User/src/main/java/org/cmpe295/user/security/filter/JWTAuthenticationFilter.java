package org.cmpe295.user.security.filter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.cmpe295.user.security.service.JWTService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    private final UserDetailsService userService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String email;
        final String userRole;
        //If no Authorization header is present in the request
        //Or if the authorization header doesn't start with Bearer
        //Pass along the filter request to the next filter in the filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        //Extract the Bearer token from request header
        jwtToken = authHeader.substring(7);
        //Extract the username from the jwt token
        email = jwtService.extractUserName(jwtToken);
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            //If you found an email/username in the jwt Token and if the Security Context is not
            //yet set in this request cycle
            //validate the details extracted from the db using email/username
            // and the token embedded details
            UserDetails userDetails = this.userService.loadUserByUsername(email);
            if (jwtService.isTokenValid(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        }
        //Pass along the request to the next filter in the filter chain
        filterChain.doFilter(request, response);
    }
}
