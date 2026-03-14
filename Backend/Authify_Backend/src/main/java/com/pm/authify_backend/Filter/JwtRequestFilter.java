package com.pm.authify_backend.Filter;

import com.pm.authify_backend.service.AppUserDetailsService;
import com.pm.authify_backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import io.jsonwebtoken.JwtException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AppUserDetailsService appUserDetailsService;

    private static final List<String> PUBLIC_URLS = List.of("/login", "/register", "/send-reset-otp", "/reset-password",
            "/logout");

    @Override
    protected void doFilterInternal(@org.springframework.lang.NonNull HttpServletRequest request,
            @org.springframework.lang.NonNull HttpServletResponse response,
            @org.springframework.lang.NonNull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        System.out.println("DEBUG: Path processing -> " + path + " | Method -> " + request.getMethod());

        if (PUBLIC_URLS.contains(path) || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = null;
        String email = null;

        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
        }
        // 2. If not Found in header check in cookies
        if (jwt == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        System.out.println("DEBUG: Found jwt cookie!");
                        break;
                    }
                }
            } else {
                System.out.println("DEBUG: cookies array is null");
            }
        }

        // 3. Validate the token and set security context

        if (jwt != null) {
            try {
                // Extract user details and set security context
                email = jwtUtil.extractEmail(jwt);
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    UserDetails userDetails = appUserDetailsService.loadUserByUsername(email);
                    if (jwtUtil.validateToken(jwt, userDetails)) {
                        System.out.println("DEBUG: Token is valid. Setting security context");
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    } else {
                        System.out.println("DEBUG: Token validation failed!");
                    }
                } else {
                    System.out.println("DEBUG: email is null or auth already set: " + email);
                }
            } catch (JwtException e) {
                System.out.println("DEBUG: JwtException: " + e.getMessage());
                // Log and ignore exception, letting Spring Security handle subsequent
                // unauthorized access
                System.out.println("Invalid or expired JWT: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("DEBUG: Gen Exception: " + e.getMessage());
            }
        }
        filterChain.doFilter(request, response);

    }
}
