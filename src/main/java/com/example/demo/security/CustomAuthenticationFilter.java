package com.example.demo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final Logger log = LoggerFactory.getLogger(CustomAuthenticationFilter.class);

    private final AuthenticationManager authenticationManager;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException {

        ObjectMapper objectMapper = new ObjectMapper();
        LoginForm form = null;
        try {
            form = objectMapper.readValue(request.getReader(), LoginForm.class);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        // TODO proper validation
        if (form == null || form.getEmail() == null || form.getPassword() == null) {
            log.error("Validation error");
            throw new RuntimeException("Validation error");
        }

        log.info("Login form: {}", form);

        String email = form.getEmail();
        String password = form.getPassword();

        log.info("{} is attempting to authenticate...", email);
        log.info("user's password is {}", password);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult
    ) throws IOException, ServletException {

        log.info("Authentication was SUCCESSFUL BABE!!!");

        User user = (User) authResult.getPrincipal();

        String access_token = JwtUtils.generateToken(
                user,
                request.getRequestURL().toString(),
                new Date(System.currentTimeMillis() + 1000 * 60 * 2)
        );

        String refresh_token = JwtUtils.generateToken(
                user,
                request.getRequestURL().toString(),
                new Date(System.currentTimeMillis() + 1000 * 60 * 5)
        );

        response.setHeader("access_token", access_token);
        response.setHeader("refresh_token", refresh_token);

        response.setContentType("application/json");

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", access_token);
        tokens.put("refresh_token", refresh_token);

        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
