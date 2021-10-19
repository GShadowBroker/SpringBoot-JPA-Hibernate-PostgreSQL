package com.example.demo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.demo.app_user.AppUser;
import com.example.demo.app_user.app_user_role.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JwtUtils {
    private static final Algorithm algorithm = Algorithm.HMAC256("This-Is-Just-A-Demo-Do-Not-Expose-Your-Secret");

    public static Algorithm getAlgorithm() {
        return algorithm;
    }

    public static String generateToken(Object user, String issuer, Date expiresAt) {
        return JWT.create()
                .withSubject(getUsername(user))
                .withExpiresAt(expiresAt)
                .withIssuer(issuer)
                .withClaim(
                        "roles",
                        getUserRoles(user)
                )
                .sign(getAlgorithm());
    }

    private static String getUsername(Object user) {
        if (user instanceof AppUser) {

            return ((AppUser) user).getEmail();

        } else if (user instanceof User) {

            return ((User) user).getUsername();
        }

        return null;
    }

    private static List<String> getUserRoles(Object user) {
        if (user instanceof AppUser) {

            return ((AppUser) user).getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());

        } else if (user instanceof User) {

            return ((User) user).getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
