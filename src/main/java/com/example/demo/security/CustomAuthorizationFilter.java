package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(CustomAuthorizationFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        if (request.getServletPath().equals("/api/v1/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Trying to get JWT token from " + request.getServletPath());

        final String header = request.getHeader(AUTHORIZATION);

        log.info(header);

        if (header == null || !header.startsWith("Bearer ")) {
            log.info("Authorization token not found or invalid");
            filterChain.doFilter(request, response);
            return;
        }

        final String token = header.split(" ")[1].trim();

        try {
            JWTVerifier verifier = JWT.require(JwtUtils.getAlgorithm()).build();

            DecodedJWT decodedJWT = verifier.verify(token);

            String email = decodedJWT.getSubject();
            String[] roles = decodedJWT.getClaim("roles").asArray(String.class);

            Collection<GrantedAuthority> authorities = new ArrayList<>();

            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority(role));
            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    email, null, authorities
            );

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error(e.getMessage());

            response.setHeader("Error", e.getMessage());
            response.setContentType("application/json");

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now().toString());
            errorResponse.put("status", String.valueOf(HttpStatus.FORBIDDEN.value()));
            errorResponse.put("error", HttpStatus.FORBIDDEN.name());
            errorResponse.put("message", e.getMessage());
            errorResponse.put("path", request.getServletPath());

            // TODO Create error class, turn class into json instead of using hash maps.
            new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);

        }
    }
}
