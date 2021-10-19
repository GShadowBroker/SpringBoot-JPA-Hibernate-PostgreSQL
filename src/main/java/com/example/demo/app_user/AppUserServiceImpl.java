package com.example.demo.app_user;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.app_user.app_user_role.Role;
import com.example.demo.app_user.app_user_role.RoleRepository;
import com.example.demo.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@Transactional
public class AppUserServiceImpl implements AppUserService, UserDetailsService {

    Logger log = LoggerFactory.getLogger(AppUserServiceImpl.class);

    @Autowired
    private AppUserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String EMAIL_NOT_FOUND_MESSAGE = "Email '%s' not found";
    private static final String ROLE_NOT_FOUND_MESSAGE = "Role '%s' not found";
    private static final String INVALID_TOKEN = "Authorization token not found or invalid";

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = getUserByEmail(email);

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return new User(user.getEmail(), user.getPassword(), authorities);
    }

    @Override
    public AppUser saveUser(AppUser user) {
        log.info("saving user to db...");

        // add user role
        Role role = getRoleByName("ROLE_USER");
        if (!user.getRoles().contains(role)) user.getRoles().add(role);

        // encode password
        long start = System.currentTimeMillis();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        long end = System.currentTimeMillis();

        log.info("Password encoded in {} ms", end - start);

        return userRepository.save(user);
    }

    @Override
    public AppUser getUserByEmail(String email) {
        log.info(String.format("getting user by email '%s'...", email));
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException(
                            String.format(EMAIL_NOT_FOUND_MESSAGE, email)
                    );
                });
    }

    @Override
    public List<AppUser> getUsers() {
        log.info("getting all users...");
        return userRepository.findAll();
    }

    @Override
    public Role saveRole(Role role) {
        log.info("saving role to db...");
        return roleRepository.save(role);
    }

    @Override
    public Role getRoleByName(String name) {
        log.info(String.format("getting role by name '%s'...", name));
        return roleRepository.getRoleByName(name)
                .orElseThrow(() -> {
                    throw new RuntimeException( // TODO make custom exception
                            String.format(ROLE_NOT_FOUND_MESSAGE, name)
                    );
                });
    }

    @Override
    public void addRoleToUser(String email, String roleName) {
        log.info("adding role to user...");

        AppUser user = getUserByEmail(email);
        Role role = getRoleByName(roleName);

        if (!user.getRoles().contains(role)) {
            user.getRoles().add(role);
        }
    }

    @Override
    public void removeRoleFromUser(String email, String roleName) {
        log.info("removing role from user");

        AppUser user = getUserByEmail(email);
        Role role = getRoleByName(roleName);

        user.getRoles().remove(role);
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        final String header = request.getHeader(AUTHORIZATION);

        log.info(header);

        if (header == null || !header.startsWith("Bearer ")) {
            log.info(INVALID_TOKEN);
            throw new RuntimeException(INVALID_TOKEN);
        }

        final String token = header.split(" ")[1].trim();

        try {
            JWTVerifier verifier = JWT.require(JwtUtils.getAlgorithm()).build();

            DecodedJWT decodedJWT = verifier.verify(token);

            String email = decodedJWT.getSubject();

            AppUser user = getUserByEmail(email);

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

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
